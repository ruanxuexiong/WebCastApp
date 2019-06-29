package com.allattentionhere.autoplayvideos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lenovo on 2018/12/21.
 */

public class FollowCustomVideosAdapter extends RecyclerView.Adapter<FollowCustomViewHolder> {


    public FollowCustomVideosAdapter() {
    }

    @Override
    public FollowCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FollowCustomViewHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(FollowCustomViewHolder holder, int position) {
    }


    @Override
    public void onViewDetachedFromWindow(final FollowCustomViewHolder holder) {
        if (holder != null && holder.getAah_vi()!=null) {
            holder.getAah_vi().getCustomVideoView().clearAll();
            holder.getAah_vi().getCustomVideoView().invalidate();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(FollowCustomViewHolder holder) {
        if (holder != null && holder.getAah_vi()!=null) {
            holder.getAah_vi().getCustomVideoView().clearAll();
            holder.getAah_vi().getCustomVideoView().invalidate();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}
