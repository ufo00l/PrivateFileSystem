package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;

import java.util.List;

public interface IIndicatorLinkedLineViewJava {
    /**
     * 绘制指示器连接线
     *
     * @param canvas
     * @param hitIndexList
     * @param cellBeanList
     * @param isError
     */
    void draw(Canvas canvas,
              List<Integer> hitIndexList,
              List<CellBeanJava> cellBeanList,
              Boolean isError);
}
