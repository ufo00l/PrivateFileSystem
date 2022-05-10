package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

import androidx.annotation.ColorInt;


public class DefaultIndicatorLinkedLineViewJava implements IIndicatorLinkedLineViewJava {
    private final DefaultStyleDecoratorJava mStyleDecorator;

    private final Paint mPaint;

    public DefaultIndicatorLinkedLineViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, List<Integer> hitIndexList, List<CellBeanJava> cellBeanList,
            Boolean isError) {
        if (!hitIndexList.isEmpty() && !cellBeanList.isEmpty()) {
            int saveCount = canvas.save();
            Path path = new Path();
            boolean first = true;

            for (Integer element$iv : hitIndexList) {
                int it = element$iv;
                if (0 <= it && it < cellBeanList.size()) {
                    CellBeanJava c = (CellBeanJava) cellBeanList.get(it);
                    if (first) {
                        path.moveTo(c.getCenterX(), c.getCenterY());
                        first = false;
                    }
                    else {
                        path.lineTo(c.getCenterX(), c.getCenterY());
                    }
                }
            }

            mPaint.setColor(this.getColor(isError));
            mPaint.setStrokeWidth(mStyleDecorator.getLineWidth());
            canvas.drawPath(path, mPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    @ColorInt
    private int getColor(boolean isError) {
        return isError ? this.mStyleDecorator.getErrorColor() : this.mStyleDecorator.getHitColor();
    }


}
