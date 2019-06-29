package com.android.nana.transition;

import android.app.Activity;


/**
 * Created by yangyu on 16/10/19.
 */

public class TransitionManager {

    private Activity mContext;

    private TransitionManager() {
    }

    public TransitionManager(Activity mContext) {
        this.mContext = mContext;
    }

    public TransitionHalfHelper getHalf() {
        return new TransitionHalfHelper(mContext);
    }

    public TransitionSingleHelper getSingle() {
        return new TransitionSingleHelper(mContext);
    }

    public TransitionMultiHelper getMulti() {
        return new TransitionMultiHelper(mContext);
    }
}
