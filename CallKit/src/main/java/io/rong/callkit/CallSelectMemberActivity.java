package io.rong.callkit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.CallkitGroupInfoEntity;
import io.rong.calllib.RongCallCommon;
import io.rong.imkit.RongContext;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.UserInfo;

/**
 * Created by weiqinxiao on 16/3/15.
 */
public class CallSelectMemberActivity extends Activity {

    ArrayList<String> selectedMember;
    TextView txtvStart;
    ListAdapter mAdapter;
    ListView mList;
    RongCallCommon.CallMediaType mMediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.rc_voip_activity_select_member);
        RongContext.getInstance().getEventBus().register(this);

        txtvStart = (TextView) findViewById(R.id.rc_btn_ok);
        txtvStart.setEnabled(false);
        txtvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("invited", selectedMember);
                setResult(RESULT_OK, intent);
                CallSelectMemberActivity.this.finish();
            }
        });
        findViewById(R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                CallSelectMemberActivity.this.finish();
            }
        });

        selectedMember = new ArrayList<>();

        Intent intent = getIntent();
        int type = intent.getIntExtra("mediaType", RongCallCommon.CallMediaType.VIDEO.getValue());
        mMediaType = RongCallCommon.CallMediaType.valueOf(type);
        final ArrayList<String> invitedMembers = intent.getStringArrayListExtra("invitedMembers");


        ArrayList<String> allMembers = intent.getStringArrayListExtra("allMembers");
        String groupId = intent.getStringExtra("groupId");
        RongCallKit.GroupMembersProvider provider = RongCallKit.getGroupMemberProvider();
        if (groupId != null && allMembers == null && provider != null) {
            allMembers = provider.getMemberList(groupId, new RongCallKit.OnGroupMembersResult() {
                @Override
                public void onGotMemberList(ArrayList<String> members) {

                }

                @Override
                public void onGetMemberList(ArrayList<CallkitGroupInfoEntity.Member> members) {
                    if (mAdapter != null) {
                        if (members != null && members.size() > 0) {
                            mAdapter.setAllMembers(members);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        if (allMembers == null) {
            allMembers = invitedMembers;
        }

        mList = (ListView) findViewById(R.id.rc_listview_select_member);
        if (invitedMembers != null && invitedMembers.size() > 0) {

            CallkitGroupInfoEntity.Member member = new CallkitGroupInfoEntity.Member();
            member.setId(allMembers.get(0));
            ArrayList<CallkitGroupInfoEntity.Member> arrayList = new ArrayList<>();
            arrayList.add(member);
            mAdapter = new ListAdapter(arrayList, invitedMembers);
            mList.setAdapter(mAdapter);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = view.findViewById(R.id.rc_checkbox);
                    CallkitGroupInfoEntity.Member member1 = (CallkitGroupInfoEntity.Member) v.getTag();
                    String userId = member1.getId();
                    if (!invitedMembers.contains(userId)) {
                        if (mMediaType.equals(RongCallCommon.CallMediaType.VIDEO)
                                && !v.isSelected() && selectedMember.size() + invitedMembers.size() >= 9) {
                            Toast.makeText(CallSelectMemberActivity.this,
                                    String.format(getString(R.string.rc_voip_over_limit), 9),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        if (selectedMember.contains(userId)) {
                            selectedMember.remove(userId);
                        }

                        v.setSelected(!v.isSelected());
                        if (v.isSelected()) {
                            selectedMember.add(userId);
                        }

                        if (selectedMember.size() > 0) {
                            txtvStart.setEnabled(true);
                            txtvStart.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            txtvStart.setEnabled(false);
                            txtvStart.setTextColor(getResources().getColor(R.color.rc_voip_check_disable));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        RongContext.getInstance().getEventBus().unregister(this);
        super.onDestroy();
    }

    class ListAdapter extends BaseAdapter {
        List<CallkitGroupInfoEntity.Member> allMembers;
        List<String> invitedMembers;

        public ListAdapter(List<CallkitGroupInfoEntity.Member> allMembers, List<String> invitedMembers) {
            this.allMembers = allMembers;
            this.invitedMembers = invitedMembers;
        }

        public void setAllMembers(List<CallkitGroupInfoEntity.Member> allMembers) {
            this.allMembers = allMembers;
        }

        @Override
        public int getCount() {
            return allMembers.size();
        }

        @Override
        public Object getItem(int position) {
            return allMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(CallSelectMemberActivity.this).inflate(R.layout.rc_voip_listitem_select_member, null);
                holder.checkbox = (ImageView) convertView.findViewById(R.id.rc_checkbox);
                holder.portrait = (AsyncImageView) convertView.findViewById(R.id.rc_user_portrait);
                holder.name = (TextView) convertView.findViewById(R.id.rc_user_name);
                holder.mIdentyIv = (ImageView) convertView.findViewById(R.id.iv_identy);
                holder.mDescribeTv = (TextView) convertView.findViewById(R.id.tv_describe);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.checkbox.setTag(allMembers.get(position));
            if (invitedMembers.contains(allMembers.get(position).getId())) {
                holder.checkbox.setClickable(false);
                holder.checkbox.setEnabled(false);
                holder.checkbox.setImageResource(R.drawable.rc_voip_icon_checkbox_checked);
            } else {
                if (selectedMember.contains(allMembers.get(position))) {
                    holder.checkbox.setImageResource(R.drawable.rc_voip_checkbox);
                    holder.checkbox.setSelected(true);
                } else {
                    holder.checkbox.setImageResource(R.drawable.rc_voip_checkbox);
                    holder.checkbox.setSelected(false);
                }
                holder.checkbox.setClickable(true);
                holder.checkbox.setEnabled(true);
            }
            CallkitGroupInfoEntity.Member member = allMembers.get(position);
            if (member != null) {
                holder.name.setText(member.getUname());
                if (null != member.getAvatar()) {
                    holder.portrait.setAvatar(Uri.parse(member.getAvatar()));
                    if (member.getStatus().equals("1")) {
                        holder.mIdentyIv.setVisibility(View.VISIBLE);
                    } else {
                        holder.mIdentyIv.setVisibility(View.GONE);
                    }

                    if (null != member.getWorkHistorys()) {
                        if (!"".equals(member.getWorkHistorys().getName()) && !"".equals(member.getWorkHistorys().getPosition())) {
                            holder.mDescribeTv.setText(member.getWorkHistorys().getPosition() + " | " + member.getWorkHistorys().getName());
                        } else if (!"".equals(member.getWorkHistorys().getName())) {
                            holder.mDescribeTv.setText(member.getWorkHistorys().getName());
                        } else if (!"".equals(member.getWorkHistorys().getPosition())) {
                            holder.mDescribeTv.setText(member.getWorkHistorys().getPosition());
                        }
                    }
                } else {//第一次加载
                    UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(allMembers.get(position).getId());
                    holder.portrait.setAvatar(userInfo.getPortraitUri());
                }



            } else {
                holder.name.setText(member.getUname());
                holder.portrait.setAvatar(null);
            }
            return convertView;
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (mList != null) {
            int first = mList.getFirstVisiblePosition() - mList.getHeaderViewsCount();
            int last = mList.getLastVisiblePosition() - mList.getHeaderViewsCount();

            int index = first - 1;

            while (++index <= last && index >= 0 && index < mAdapter.getCount()) {
                if (mAdapter.getItem(index).equals(userInfo.getUserId())) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount()), mList);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ViewHolder {
        ImageView checkbox,mIdentyIv;
        AsyncImageView portrait;
        TextView name,mDescribeTv;
    }
}
