package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.ColorInt;

public class DefaultIndicatorHitCellViewJava implements IHitCellViewJava {

    DefaultStyleDecoratorJava mStyleDecorator;
    Paint mPaint;

    public DefaultIndicatorHitCellViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(@NotNull Canvas canvas, @NotNull CellBeanJava cellBean, boolean isError) {
        int saveCount = canvas.save();
        mPaint.setColor(this.getColor(isError));
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(), cellBean.getRadius(),
                mPaint);
        canvas.restoreToCount(saveCount);
    }

    @ColorInt
    private final int getColor(boolean isError) {
        return isError ? mStyleDecorator.getErrorColor() : mStyleDecorator.getHitColor();
    }
}
