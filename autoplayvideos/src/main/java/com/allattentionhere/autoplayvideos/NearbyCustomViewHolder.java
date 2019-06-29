package com.allattentionhere.autoplayvideos;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.Callable;

/**
 * Created by lenovo on 2018/12/21.
 */

public class NearbyCustomViewHolder extends RecyclerView.ViewHolder {

    private NearbyCustomVideoImage aah_vi;
    private String imageUrl;
    private String videoUrl;
    private boolean isLooping = true;
    private boolean isPaused = false;


    public NearbyCustomViewHolder(View x) {
        super(x);
        aah_vi = (NearbyCustomVideoImage) x.findViewWithTag("nearby_aah_vi");
    }

    public void playVideo() {
        this.aah_vi.getCustomVideoView().setPaused(false);
        this.aah_vi.getCustomVideoView().startVideo();
    }


    public void videoStarted() {
        this.aah_vi.getImageView().setVisibility(View.GONE);
    }

    public void showThumb() {
        this.aah_vi.getImageView().setVisibility(View.VISIBLE);
    }

    public void initVideoView(String url, Activity _act) {
        this.aah_vi.getCustomVideoView().setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(url);
        this.aah_vi.getCustomVideoView().setSource(uri);
        this.aah_vi.getCustomVideoView().setLooping(isLooping);
        this.aah_vi.getCustomVideoView().set_act(_act);
        this.aah_vi.getCustomVideoView().setMyFuncIn(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                videoStarted();
                return null;
            }
        });

        this.aah_vi.getCustomVideoView().setShowThumb(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                showThumb();
                return null;
            }
        });
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public void pauseVideo() {
        this.aah_vi.getCustomVideoView().pauseVideo();
        this.aah_vi.getCustomVideoView().setPaused(true);
    }



    public void muteVideo() {
        this.aah_vi.getCustomVideoView().muteVideo();
    }

    public void unmuteVideo() {
        this.aah_vi.getCustomVideoView().unmuteVideo();
    }

    public NearbyCustomVideoImage getAah_vi() {
        return aah_vi;
    }

    public ImageView getAAH_ImageView() {
        return aah_vi.getImageView();
    }

    public String getImageUrl() {
        return imageUrl + "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if (null != this.aah_vi.getImageView()) {
            this.aah_vi.getImageView().setVisibility(View.VISIBLE);
        }
    }

    public void setAah_vi(NearbyCustomVideoImage aah_vi) {
        this.aah_vi = aah_vi;
    }

    public String getVideoUrl() {
        return videoUrl + "";
    }

    public boolean isPlaying() {
        return this.aah_vi.getCustomVideoView().isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return this.aah_vi.getCustomVideoView().getMediaPlayer();
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isLooping() {
        return isLooping;
    }
}
