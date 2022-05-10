package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.ColorInt;

public class DefaultLockerHitCellViewJava implements IHitCellViewJava {
    private final DefaultStyleDecoratorJava mStyleDecorator;

    private final Paint mPaint;

    public DefaultLockerHitCellViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void draw(@NotNull Canvas canvas, @NotNull CellBeanJava cellBean, boolean isError) {
        int saveCount = canvas.save();
        this.mPaint.setColor(this.getColor(isError));
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(), cellBean.getRadius(),
                mPaint);
        this.mPaint.setColor(this.mStyleDecorator.getFillColor());
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(),
                cellBean.getRadius() - this.mStyleDecorator.getLineWidth(), mPaint);
        this.mPaint.setColor(this.getColor(isError));
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(), cellBean.getRadius() / 5.0F,
                mPaint);
        canvas.restoreToCount(saveCount);
    }

    @ColorInt
    private final int getColor(boolean isError) {
        return isError ? this.mStyleDecorator.getErrorColor() : this.mStyleDecorator.getHitColor();
    }

}
