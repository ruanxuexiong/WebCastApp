package com.android.nana.widget;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by lenovo on 2017/8/4.
 */

public class InputFilterMinMax implements InputFilter {


    private int max;
    private double min;

    public InputFilterMinMax(double min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            double input = Double.valueOf(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, int b, double c) {
        if (a > c) {
            return a > c;
        } else {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
