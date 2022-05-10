package com.ljq.privatefiledemo.patternlockerjava;

import java.util.List;

public interface OnPatternChangeListenerJava {
    /**
     * 开始绘制图案时（即手指按下触碰到绘画区域时）会调用该方法
     *
     * @param view
     */
    void onStart(PatternLockerViewJava view);

    /**
     * 图案绘制改变时（即手指在绘画区域移动时）会调用该方法，请注意只有 @param hitList改变了才会触发此方法
     *
     * @param view
     * @param hitIndexList
     */
    void onChange(PatternLockerViewJava view, List<Integer> hitIndexList);

    /**
     * 图案绘制完成时（即手指抬起离开绘画区域时）会调用该方法
     *
     * @param view
     * @param hitIndexList
     */
    void onComplete(PatternLockerViewJava view, List<Integer> hitIndexList);

    /**
     * 已绘制的图案被清除时会调用该方法
     *
     * @param view
     */
    void onClear(PatternLockerViewJava view);
}
