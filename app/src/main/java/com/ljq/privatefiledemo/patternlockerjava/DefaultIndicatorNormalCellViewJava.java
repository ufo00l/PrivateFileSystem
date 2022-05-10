package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;


public class DefaultIndicatorNormalCellViewJava {
    private final DefaultStyleDecoratorJava mStyleDecorator;

    private final Paint mPaint;

    public DefaultIndicatorNormalCellViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void draw(@NotNull Canvas canvas, @NotNull CellBeanJava cellBean) {
        int saveCount = canvas.save();
        mPaint.setColor(this.mStyleDecorator.getNormalColor());
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(), cellBean.getRadius(),
                mPaint);
        mPaint.setColor(this.mStyleDecorator.getFillColor());
        canvas.drawCircle(cellBean.getCenterX(), cellBean.getCenterY(),
                cellBean.getRadius() - this.mStyleDecorator.getLineWidth(), mPaint);
        canvas.restoreToCount(saveCount);
    }

    @NotNull
    public final DefaultStyleDecoratorJava getStyleDecorator() {
        return this.mStyleDecorator;
    }

}
