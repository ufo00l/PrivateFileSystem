package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;

public interface IHitCellViewJava {
    /**
     * 绘制已设置的每个图案的样式
     *
     * @param canvas
     * @param cellBean
     * @param isError
     */
    void draw(Canvas canvas, CellBeanJava cellBean, boolean isError);
}
