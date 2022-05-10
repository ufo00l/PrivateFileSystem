package com.ljq.privatefiledemo.patternlockerjava;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

public class DefaultConfigJava {
    private static DefaultConfigJava instance;

    public static DefaultConfigJava getInstance() {
        if (instance == null) {
            instance = new DefaultConfigJava();
        }
        return instance;
    }

    private static final String DEFAULT_NORMAL_COLOR = "#2196F3";
    private static final String DEFAULT_HIT_COLOR = "#3F51B5";
    private static final String DEFAULT_ERROR_COLOR = "#F44336";
    private static final String DEFAULT_FILL_COLOR = "#FFFFFF";
    private static final int DEFAULT_LINE_WIDTH = 1;

    //ms
    static final int defaultFreezeDuration = 1000;
    static final boolean defaultEnableAutoClean = true;
    static final boolean defaultEnableHapticFeedback = false;
    static final boolean defaultEnableSkip = false;
    static final boolean defaultEnableLogger = false;

    public int getDefaultNormalColor() {
        return defaultNormalColor;
    }

    public int getDefaultHitColor() {
        return defaultHitColor;
    }

    public int getDefaultErrorColor() {
        return defaultErrorColor;
    }

    public int getDefaultFillColor() {
        return defaultFillColor;
    }

    @ColorInt
    int defaultNormalColor = Color.parseColor(DEFAULT_NORMAL_COLOR);

    @ColorInt
    int defaultHitColor = Color.parseColor(DEFAULT_HIT_COLOR);

    @ColorInt
    int defaultErrorColor = Color.parseColor(DEFAULT_ERROR_COLOR);

    @ColorInt
    int defaultFillColor = Color.parseColor(DEFAULT_FILL_COLOR);

    float getDefaultLineWidth(Resources resources) {
        return convertDpToPx((float) DEFAULT_LINE_WIDTH, resources);
    }

    static Paint createPaint() {
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private float convertDpToPx(float dpValue, Resources resources) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                resources.getDisplayMetrics());
    }
}
