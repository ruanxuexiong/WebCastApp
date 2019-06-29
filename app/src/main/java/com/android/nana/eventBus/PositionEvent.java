package com.android.nana.eventBus;

import com.android.nana.wanted.Position;

/**
 * Created by THINK on 2017/7/20.
 */

public class PositionEvent {
    public String position;
    public Position mPosition;

    public PositionEvent(String position) {
        this.position = position;
    }

    public PositionEvent(Position mPosition) {
        this.mPosition = mPosition;
    }

}
