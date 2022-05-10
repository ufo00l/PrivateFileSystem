package com.ljq.privatefiledemo.patternlockerjava;

import android.graphics.Canvas;

import java.util.List;

public interface ILockerLinkedLineViewJava {
    /**
     * 绘制图案密码连接线
     *
     * @param canvas
     * @param hitIndexList
     * @param cellBeanList
     * @param endX
     * @param endY
     * @param isError
     */
    void draw(Canvas canvas,
              List<Integer> hitIndexList,
              List<CellBeanJava> cellBeanList,
              float endX,
              float endY,
              boolean isError);
}
