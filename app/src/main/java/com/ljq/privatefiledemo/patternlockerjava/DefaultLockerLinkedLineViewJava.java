package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.ColorInt;

public class DefaultLockerLinkedLineViewJava implements ILockerLinkedLineViewJava {
    private final DefaultStyleDecoratorJava mStyleDecorator;

    private final Paint mPaint;

    public DefaultLockerLinkedLineViewJava(DefaultStyleDecoratorJava styleDecorator) {
        mStyleDecorator = styleDecorator;
        mPaint = DefaultConfigJava.createPaint();
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void draw(@NotNull Canvas canvas,
            @NotNull List<Integer> hitIndexList, @NotNull List<CellBeanJava> cellBeanList,
            float endX,
            float endY,
            boolean isError) {
        if (!hitIndexList.isEmpty() && !cellBeanList.isEmpty()) {
            int saveCount = canvas.save();
            Path path = new Path();
            boolean first = true;

            for (Object element$iv : hitIndexList) {
                int it = ((Number) element$iv).intValue();
                if (0 <= it && it < cellBeanList.size()) {
                    CellBeanJava c = cellBeanList.get(it);
                    if (first) {
                        path.moveTo(c.getCenterX(), c.getCenterY());
                        first = false;
                    }
                    else {
                        path.lineTo(c.getCenterX(), c.getCenterY());
                    }
                }
            }

            if ((endX != 0.0F || endY != 0.0F) && hitIndexList.size() < 9) {
                path.lineTo(endX, endY);
            }

            this.mPaint.setColor(this.getColor(isError));
            this.mPaint.setStrokeWidth(this.mStyleDecorator.getLineWidth());
            canvas.drawPath(path, this.mPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    @ColorInt
    private int getColor(boolean isError) {
        return isError ? this.mStyleDecorator.getErrorColor() : this.mStyleDecorator.getHitColor();
    }
}
