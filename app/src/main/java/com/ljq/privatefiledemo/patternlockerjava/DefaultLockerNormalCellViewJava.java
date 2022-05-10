package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DefaultLockerNormalCellViewJava implements INormalCellViewJava {

    public DefaultStyleDecoratorJava getStyleDecorator() {
        return mStyleDecorator;
    }

    private final DefaultStyleDecoratorJava mStyleDecorator;
    private final Paint mPaint;

    public DefaultLockerNormalCellViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, CellBeanJava cellBean) {
        int saveCount = canvas.save();
        // draw outer circle
        this.mPaint.setColor(this.mStyleDecorator.getNormalColor());
        canvas.drawCircle(cellBean.centerX, cellBean.centerY, cellBean.radius, this.mPaint);

        // draw fill circle
        this.mPaint.setColor(this.mStyleDecorator.getFillColor());
        canvas.drawCircle(cellBean.centerX, cellBean.centerY,
                cellBean.radius - this.mStyleDecorator.getLineWidth(), this.mPaint);

        canvas.restoreToCount(saveCount);
    }


}
