package com.android.nana.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.LatelyAdapter;
import com.android.nana.adapter.SharedAdapter;
import com.android.nana.adapter.SharedGroupAdapter;
import com.android.nana.auth.LoginCodeActivity;
import com.android.nana.bean.SharedEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.main.MainActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.widget.LinkPreviewCallback;
import com.android.nana.widget.SourceContent;
import com.android.nana.widget.TextCrawler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;

/**
 * Created by lenovo on 2017/10/19.
 */

public class SharedReceiverActivity extends BaseActivity implements View.OnClickListener, LatelyAdapter.LateInterface, SharedAdapter.SharedListener, SharedGroupAdapter.SharedGroupListener {


    private TextView mTitleTv;
    private TextView mBackTv;
    private String mTitle;
    private boolean mIsPlainNormalText;
    private ListView mFriendslv, mGrouplv;
    private List<Conversation> conversationsList;
    private List<Groups> mGroupData;
    private List<Friend> mFriendData;
    private LatelyAdapter mAdapter;
    private String mid;

    private LinearLayout mFriendLl, mGroupLl;
    private SharedAdapter mUsersAdapter;
    private SharedGroupAdapter mGroupAdapter;
    private ArrayList<SharedEntity.Users> users = new ArrayList<>();
    private ArrayList<SharedEntity.GroupList> group = new ArrayList<>();
    private List<NewConversation> newConversationsList = new ArrayList<>();
    private FileMessage fileMessage;
    private ImageMessage imageMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            /** 截获 Intent 部分 **/
            try {
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    String linkInfo = null;
                    if (null != getIntent().getClipData()) {
                        linkInfo = getIntent().getClipData().toString();
                    }

                    if (linkInfo != null) {
                        if (linkInfo.contains("file://")) {
                            ToastUtils.showToast("暂时不支持");
                            finish();
                            return;
                        }
                        String titleInfo = null;
                        int start = linkInfo.indexOf("T:");
                        int end = linkInfo.indexOf("http");
                        if (-1 != start && end > start) {
                            titleInfo = linkInfo.substring(start, end).trim();
                        }
                        if (titleInfo != null && titleInfo.length() > 3) {
                            mTitle = titleInfo.substring(3, titleInfo.length());
                        }
                    }
                }
                String action = intent.getAction();
                String type = intent.getType();
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if (type.equals("image/jpeg")) {
                        mTitle = "截图分享";
                        Uri fileUri = getIntent().getClipData().getItemAt(0).getUri();
                        String path = Utils.getRealPathFromUri(SharedReceiverActivity.this, fileUri);
                        File file = new File(path);
                        Uri thumb = Uri.fromFile(file);
                        imageMessage = new ImageMessage();
                        imageMessage.obtain(thumb, thumb, true);
                        imageMessage.setLocalUri(thumb);
                        imageMessage.setThumUri(thumb);
                    }
                }

                if (null != getIntent().getData()) {
                    String uriString = getIntent().getData().getPath();
                    Uri filePath = Uri.parse("file://" + uriString);
                    fileMessage = FileMessage.obtain(filePath);
                }

                if (getIntent().getExtras() != null) {
                    String shareVia = (String) getIntent().getExtras().get(Intent.EXTRA_TEXT);
                    if (shareVia != null) {
                        if (shareVia.toLowerCase().contains("http://")
                                || shareVia.toLowerCase().contains("https://")) {
                            TextCrawler textCrawler = new TextCrawler();
                            textCrawler.makePreview(callback, shareVia);
                        } else {
                            mIsPlainNormalText = true;
                            description = shareVia;
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                finish();
                return;
            }
        }

        //  showProgressDialog("", "加载中...");
        mid = (String) SharedPreferencesUtils.getParameter(SharedReceiverActivity.this, "userId", "");
        if (null != mid) {

            if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                getConversations();
            } else {
                String cacheToken = getSharedPreferences("config", MODE_PRIVATE).getString("loginToken", "");
                if (!TextUtils.isEmpty(cacheToken)) {
                    RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {

                        }

                        @Override
                        public void onSuccess(String s) {
                            dismissProgressDialog();
                            getConversations();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                }
            }

        } else {
            Intent intent1 = new Intent(new Intent(SharedReceiverActivity.this, LoginCodeActivity.class));
            startActivity(intent1);
        }

    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_share_receiver);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mFriendslv = findViewById(R.id.lv_friends);
        mGrouplv = findViewById(R.id.lv_group);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mFriendLl = findViewById(R.id.ll_lv_friends);
        mGroupLl = findViewById(R.id.ll_lv_group);
    }

    @Override
    protected void init() {
        initView();
    }

    private void getConversations() {
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
        };
        if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
            RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                @Override
                public void onSuccess(List<Conversation> conversations) {
                    dismissProgressDialog();
                    conversationsList = conversations;
                    initData();
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            }, conversationTypes);
        }
    }

    private void initData() {
        HomeDbHelper.transfriend(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        users = parseUsersData(successJson);
                        group = parseGroupData(successJson);

                        if (users.size() > 0) {
                            mUsersAdapter = new SharedAdapter(SharedReceiverActivity.this, users, SharedReceiverActivity.this);
                            mFriendslv.setAdapter(mUsersAdapter);
                            mUsersAdapter.notifyDataSetChanged();
                            mFriendLl.setVisibility(View.VISIBLE);
                        } else {
                            mFriendLl.setVisibility(View.GONE);
                        }

                        if (group.size() > 0) {
                            mGroupAdapter = new SharedGroupAdapter(SharedReceiverActivity.this, group, SharedReceiverActivity.this);
                            mGrouplv.setAdapter(mGroupAdapter);
                            mGroupAdapter.notifyDataSetChanged();
                            mGroupLl.setVisibility(View.VISIBLE);
                        } else {
                            mGroupLl.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    private void initView() {
        mTitleTv.setText("选择联系人");
        mBackTv.setVisibility(View.VISIBLE);


        mGrouplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    ToastUtils.showToast("网络不可用");
                    return;
                }
                if (newConversationsList != null) {
                    final AlertDialog dlg = new AlertDialog.Builder(SharedReceiverActivity.this).create();
                    dlg.show();
                    dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    final Window window = dlg.getWindow();
                    window.setContentView(R.layout.share_dialog);
                    Button ok = (Button) window.findViewById(R.id.share_ok);
                    Button cancel = (Button) window.findViewById(R.id.share_cancel);
                    TextView content = (TextView) window.findViewById(R.id.share_cotent);
                    TextView from = (TextView) window.findViewById(R.id.share_from);
                    ImageView image = (ImageView) window.findViewById(R.id.share_image);
                    TextView title = (TextView) window.findViewById(R.id.share_title);

                    if (!TextUtils.isEmpty(description)) {
                        content.setText(description);
                    }
                    if (mIsPlainNormalText) {
                        title.setVisibility(View.GONE);
                        image.setVisibility(View.GONE);
                        from.setVisibility(View.GONE);
                    } else {
                        if (TextUtils.isEmpty(mTitle)) {
                            title.setText(titleString);
                        } else {
                            title.setText(mTitle);
                        }
                        if (!TextUtils.isEmpty(imageString)) {
                            ImageLoader.getInstance().displayImage(imageString, image);
                        }
                        if (!TextUtils.isEmpty(fromString)) {
                            from.setText(getString(R.string.ac_share_receiver_from, fromString));
                        } else {
                            from.setVisibility(View.GONE);
                        }
                    }
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Conversation.ConversationType conversationType = newConversationsList.get(position).getmConversationType();
                            String targetId = newConversationsList.get(position).getTargetId();
                            showProgressDialog("", "加载中...");
                            final EditText say = (EditText) window.findViewById(R.id.share_say);
                            String remindText = say.getText().toString();
                            if (!TextUtils.isEmpty(remindText)) {
                                sendRemindMessage(conversationType, targetId, remindText);
                            }

                            //NLog.e("share", "分享:" + titleString + "\n" + finalUri + "\n" + "来自:" + fromString);
                            if (mIsPlainNormalText) {
                                sendShareMessage(conversationType, targetId, TextMessage.obtain(description));
                            } else {
                                RichContentMessage richContentMessage;
                                if (TextUtils.isEmpty(mTitle)) {
                                    richContentMessage = RichContentMessage.obtain(titleString, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                                } else {
                                    richContentMessage = RichContentMessage.obtain(mTitle, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                                }
                                sendShareMessage(conversationType, targetId, richContentMessage);
                            }
                            dlg.cancel();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dlg.cancel();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, CheckBox mCheckBox, int position) {

    }

    @Override
    public void onContentClick(View view) {

        final SharedEntity.Users item = users.get((Integer) view.getTag());
        if (null != imageMessage) {
            RongIM.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, item.getId(), imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    dismissProgressDialog();
                    ToastUtils.showToast("分享失败，请稍后重试！");
                }

                @Override
                public void onSuccess(Message message) {
                    dismissProgressDialog();
                    ToastUtils.showToast("分享成功！");
                    startActivity(new Intent(SharedReceiverActivity.this, MainActivity.class));
                    SharedReceiverActivity.this.finish();
                }

                @Override
                public void onProgress(Message message, int i) {

                }
            });

        } else if (null != fileMessage) {

            Message message = Message.obtain(item.getId(), Conversation.ConversationType.PRIVATE, fileMessage);
            sendMediaMessage(message);
        } else {

          /*  if (null == fileMessage) {
                ToastUtils.showToast("当前版本暂不支持查看此消息");
                return;
            }*/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                ToastUtils.showToast("网络不可用");
                return;
            }

            if (newConversationsList != null) {
                final AlertDialog dlg = new AlertDialog.Builder(SharedReceiverActivity.this).create();
                dlg.show();
                dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                final Window window = dlg.getWindow();
                window.setContentView(R.layout.share_dialog);
                Button ok = window.findViewById(R.id.share_ok);
                Button cancel = window.findViewById(R.id.share_cancel);
                TextView content = window.findViewById(R.id.share_cotent);
                TextView from = window.findViewById(R.id.share_from);
                ImageView image = window.findViewById(R.id.share_image);
                TextView title = window.findViewById(R.id.share_title);

                if (!TextUtils.isEmpty(description)) {
                    content.setText(description);
                }
                if (mIsPlainNormalText) {
                    title.setVisibility(View.GONE);
                    image.setVisibility(View.GONE);
                    from.setVisibility(View.GONE);
                } else {
                    if (TextUtils.isEmpty(mTitle)) {
                        title.setText(titleString);
                    } else {
                        title.setText(mTitle);
                    }
                    if (!TextUtils.isEmpty(imageString)) {
                        ImageLoader.getInstance().displayImage(imageString, image);
                    }
                    if (!TextUtils.isEmpty(fromString)) {
                        from.setText(getString(R.string.ac_share_receiver_from, fromString));
                    } else {
                        from.setVisibility(View.GONE);
                    }
                }
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText say = (EditText) window.findViewById(R.id.share_say);
                        String remindText = say.getText().toString();
                        if (!TextUtils.isEmpty(remindText)) {
                            sendRemindMessage(Conversation.ConversationType.PRIVATE, item.getId(), remindText);
                        }

                        if (mIsPlainNormalText) {
                            sendShareMessage(Conversation.ConversationType.PRIVATE, item.getId(), TextMessage.obtain(description));
                        } else {
                            RichContentMessage richContentMessage;
                            if (TextUtils.isEmpty(mTitle)) {
                                richContentMessage = RichContentMessage.obtain(titleString, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                            } else {
                                richContentMessage = RichContentMessage.obtain(mTitle, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                            }
                            sendShareMessage(Conversation.ConversationType.PRIVATE, item.getId(), richContentMessage);
                        }
                        dlg.cancel();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.cancel();
                    }
                });
            }
        }
    }

    @Override
    public void onItemClick(View view) {
        final SharedEntity.GroupList item = group.get((Integer) view.getTag());

        if (null != imageMessage) {
            RongIM.getInstance().sendImageMessage(Conversation.ConversationType.GROUP, item.getGroupId(), imageMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    dismissProgressDialog();
                    ToastUtils.showToast("分享失败，请稍后重试！");
                    SharedReceiverActivity.this.finish();
                }

                @Override
                public void onSuccess(Message message) {
                    dismissProgressDialog();
                    ToastUtils.showToast("分享成功！");
                    startActivity(new Intent(SharedReceiverActivity.this, MainActivity.class));
                    SharedReceiverActivity.this.finish();
                }

                @Override
                public void onProgress(Message message, int i) {

                }
            });

        } else if (null != fileMessage) {
            Message message = Message.obtain(item.getGroupId(), Conversation.ConversationType.GROUP, fileMessage);
            sendMediaMessage(message);
        } else {

           /* if (null == fileMessage) {
                ToastUtils.showToast("当前版本暂不支持查看此消息");
                return;
            }*/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                ToastUtils.showToast("网络不可用");
                return;
            }

            if (newConversationsList != null) {
                final AlertDialog dlg = new AlertDialog.Builder(SharedReceiverActivity.this).create();
                dlg.show();
                dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                final Window window = dlg.getWindow();
                window.setContentView(R.layout.share_dialog);
                Button ok = (Button) window.findViewById(R.id.share_ok);
                Button cancel = (Button) window.findViewById(R.id.share_cancel);
                TextView content = (TextView) window.findViewById(R.id.share_cotent);
                TextView from = (TextView) window.findViewById(R.id.share_from);
                ImageView image = (ImageView) window.findViewById(R.id.share_image);
                TextView title = (TextView) window.findViewById(R.id.share_title);

                if (!TextUtils.isEmpty(description)) {
                    content.setText(description);
                }
                if (mIsPlainNormalText) {
                    title.setVisibility(View.GONE);
                    image.setVisibility(View.GONE);
                    from.setVisibility(View.GONE);
                } else {
                    if (TextUtils.isEmpty(mTitle)) {
                        title.setText(titleString);
                    } else {
                        title.setText(mTitle);
                    }
                    if (!TextUtils.isEmpty(imageString)) {
                        ImageLoader.getInstance().displayImage(imageString, image);
                    }
                    if (!TextUtils.isEmpty(fromString)) {
                        from.setText(getString(R.string.ac_share_receiver_from, fromString));
                    } else {
                        from.setVisibility(View.GONE);
                    }
                }
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText say = (EditText) window.findViewById(R.id.share_say);
                        String remindText = say.getText().toString();
                        if (!TextUtils.isEmpty(remindText)) {
                            sendRemindMessage(Conversation.ConversationType.GROUP, item.getGroupId(), remindText);
                        }

                        if (mIsPlainNormalText) {
                            sendShareMessage(Conversation.ConversationType.GROUP, item.getGroupId(), TextMessage.obtain(description));
                        } else {
                            RichContentMessage richContentMessage;
                            if (TextUtils.isEmpty(mTitle)) {
                                richContentMessage = RichContentMessage.obtain(titleString, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                            } else {
                                richContentMessage = RichContentMessage.obtain(mTitle, TextUtils.isEmpty(description) ? finalUri : description, imageString, finalUri);
                            }
                            sendShareMessage(Conversation.ConversationType.GROUP, item.getGroupId(), richContentMessage);
                        }
                        dlg.cancel();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.cancel();
                    }
                });

            }
        }
    }

    class NewConversation {
        Conversation.ConversationType mConversationType;
        String targetId;
        String portraitUri;
        String title;

        public NewConversation(Conversation.ConversationType mConversationType, String targetId, String portraitUri, String title) {
            this.mConversationType = mConversationType;
            this.targetId = targetId;
            this.portraitUri = portraitUri;
            this.title = title;
        }

        public Conversation.ConversationType getmConversationType() {
            return mConversationType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


    private String imageString;

    private String fromString;

    private String description;

    private String titleString;

    private String finalUri;
    /** Callback to update your view. Totally customizable. */
    /** onPre() will be called before the crawling. onPos() after. */
    /**
     * You can customize this to update your view
     */
    private LinkPreviewCallback callback = new LinkPreviewCallback() {

        @Override
        public void onPre() {
            showProgressDialog("", "正在加载...");
        }

        @Override
        public void onPos(SourceContent sourceContent, boolean isNull) {
            if (sourceContent != null) {

                if (sourceContent.getImages().size() > 0) {
                    imageString = sourceContent.getImages().get(0);
                }
                fromString = sourceContent.getCannonicalUrl();
                description = sourceContent.getDescription();
                titleString = sourceContent.getTitle();
                finalUri = sourceContent.getFinalUrl();
                dismissProgressDialog();
            }
        }
    };


    private void sendRemindMessage(Conversation.ConversationType conversationType, String targetId, String content) {
        RongIM.getInstance().sendMessage(conversationType, targetId, TextMessage.obtain(content), null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode e) {

            }

            @Override
            public void onSuccess(Integer integer) {

            }
        });
    }

    private void sendMediaMessage(Message message) {
        RongIM.getInstance().sendMediaMessage(message, "file", null, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {

            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                SharedReceiverActivity.this.finish();
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtils.showToast("分享失败，请稍后再试！");
            }
        });
    }

    private void sendShareMessage(Conversation.ConversationType conversationType, String targetId, MessageContent content) {
        RongIM.getInstance().sendMessage(conversationType, targetId, content, null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode e) {
                dismissProgressDialog();
                ToastUtils.showToast("分享失败");
            }

            @Override
            public void onSuccess(Integer integer) {
                dismissProgressDialog();
                ToastUtils.showToast("分享成功");
                startActivity(new Intent(SharedReceiverActivity.this, MainActivity.class));
                SharedReceiverActivity.this.finish();
            }
        });
    }

    public ArrayList<SharedEntity.GroupList> parseGroupData(String result) throws JSONException {//好友 解析
        ArrayList<SharedEntity.GroupList> detail = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(result);
        JSONObject data = new JSONObject(jsonobject.getString("data"));
        JSONArray group = new JSONArray(data.getString("groupList"));

        if (group.length() > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < group.length(); i++) {
                SharedEntity.GroupList item = gson.fromJson(group.optJSONObject(i).toString(), SharedEntity.GroupList.class);
                detail.add(item);
            }
        }
        return detail;
    }

    public ArrayList<SharedEntity.Users> parseUsersData(String result) throws JSONException {//好友 解析
        ArrayList<SharedEntity.Users> detail = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(result);
        JSONObject data = new JSONObject(jsonobject.getString("data"));
        JSONArray users = new JSONArray(data.getString("users"));

        if (users.length() > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < users.length(); i++) {
                SharedEntity.Users item = gson.fromJson(users.optJSONObject(i).toString(), SharedEntity.Users.class);
                detail.add(item);
            }
        }
        return detail;
    }
}
