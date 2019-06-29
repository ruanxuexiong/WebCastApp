package com.android.nana.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.Ranking;
import com.android.nana.ui.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijianchang@yy.com on 2017/4/12.
 */

public class HelpOtherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Ranking.DataBean> datas;
    private Context context;
    private int normalType = 0;
    private int footType = 1;
    private boolean hasMore = true;
    private boolean fadeTips = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public HelpOtherAdapter(List<Ranking.DataBean> datas, Context context, boolean hasMore) {
        this.datas = datas;
        this.context = context;
        this.hasMore = hasMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == normalType) {
            return new NormalHolder(LayoutInflater.from(context).inflate(R.layout.item_ranking, null));
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview, null));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            if (datas.get(position).getIndex() == 1) {
                ((NormalHolder) holder).tv_1.setVisibility(View.GONE);
                ((NormalHolder) holder).img_bg.setVisibility(View.VISIBLE);
                ((NormalHolder) holder).img_bg.setBackground(context.getResources().getDrawable(R.drawable.f_bg));
            } else if (datas.get(position).getIndex() == 2) {
                ((NormalHolder) holder).tv_1.setVisibility(View.GONE);
                ((NormalHolder) holder).img_bg.setVisibility(View.VISIBLE);
                ((NormalHolder) holder).img_bg.setBackground(context.getResources().getDrawable(R.drawable.s_bg));
            } else if (datas.get(position).getIndex() == 3) {
                ((NormalHolder) holder).tv_1.setVisibility(View.GONE);
                ((NormalHolder) holder).img_bg.setVisibility(View.VISIBLE);
                ((NormalHolder) holder).img_bg.setBackground(context.getResources().getDrawable(R.drawable.t_bg));
            } else {
                ((NormalHolder) holder).tv_1.setVisibility(View.VISIBLE);
                ((NormalHolder) holder).tv_1.setText(datas.get(position).getIndex() + "");
                ((NormalHolder) holder).img_bg.setVisibility(View.GONE);
            }
            if (position == (datas.size() - 1))
                ((NormalHolder) holder).line_view.setVisibility(View.GONE);
            else
                ((NormalHolder) holder).line_view.setVisibility(View.VISIBLE);
            ((NormalHolder) holder).tv_2.setText(datas.get(position).getUsername());
            ((NormalHolder) holder).tv_3.setText(datas.get(position).getTotal());
            ((NormalHolder) holder).tv_4.setText(datas.get(position).getEarnings());
            ImgLoaderManager.getInstance().showImageView(datas.get(position).getAvatar(), ((NormalHolder) holder).roundImageView);

        } else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if (hasMore == true) {
                fadeTips = false;
                if (datas.size() > 0) {
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (datas.size() > 0) {
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public int getRealLastPosition() {
        return datas.size();
    }


    public void updateList(List<Ranking.DataBean> newDatas, boolean hasMore,boolean hasRefresh) {
        if (newDatas != null) {
            if (hasRefresh){
                datas.clear();
            }
            datas.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        private TextView tv_1, tv_2, tv_3, tv_4;
        private RoundImageView roundImageView;
        private ImageView img_bg;
        private View line_view;

        public NormalHolder(View itemView) {
            super(itemView);
            line_view = itemView.findViewById(R.id.line_view);
            img_bg = itemView.findViewById(R.id.img_bg);
            tv_1 = itemView.findViewById(R.id.tv_1);
            tv_2 = itemView.findViewById(R.id.tv_2);
            tv_3 = itemView.findViewById(R.id.tv_3);
            tv_4 = itemView.findViewById(R.id.tv_4);
            roundImageView = itemView.findViewById(R.id.iv_avatar);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public void resetDatas() {
        datas = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
}
