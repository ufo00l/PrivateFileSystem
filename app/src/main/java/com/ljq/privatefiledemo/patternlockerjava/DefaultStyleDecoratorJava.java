package com.ljq.privatefiledemo.patternlockerjava;

import androidx.annotation.ColorInt;

public class DefaultStyleDecoratorJava {
    @ColorInt
    int normalColor;
    @ColorInt
    int fillColor;
    @ColorInt
    int hitColor;
    @ColorInt
    int errorColor;
    float lineWidth;

    public DefaultStyleDecoratorJava(int normalColor, int fillColor, int hitColor, int errorColor,
            float lineWidth) {
        this.normalColor = normalColor;
        this.fillColor = fillColor;
        this.hitColor = hitColor;
        this.errorColor = errorColor;
        this.lineWidth = lineWidth;
    }

    public int getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getHitColor() {
        return hitColor;
    }

    public void setHitColor(int hitColor) {
        this.hitColor = hitColor;
    }

    public int getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
}
