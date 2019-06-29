package com.allattentionhere.autoplayvideos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lenovo on 2018/12/21.
 */

public class NearbyCustomVideosAdapter  extends RecyclerView.Adapter<NearbyCustomViewHolder> {


    public NearbyCustomVideosAdapter() {
    }

    @Override
    public NearbyCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NearbyCustomViewHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(NearbyCustomViewHolder holder, int position) {
    }


    @Override
    public void onViewDetachedFromWindow(final NearbyCustomViewHolder holder) {
        if (holder != null && holder.getAah_vi()!=null) {
            holder.getAah_vi().getCustomVideoView().clearAll();
            holder.getAah_vi().getCustomVideoView().invalidate();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(NearbyCustomViewHolder holder) {
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
