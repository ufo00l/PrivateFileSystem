package com.ljq.privatefiledemo.patternlockerjava;

import java.util.ArrayList;
import java.util.List;

public class CellFactoryJava {

    public static List<CellBeanJava> buildCells(int width, int height) {
        ArrayList<CellBeanJava> result = new ArrayList<CellBeanJava>();
        float pWidth = width / 8f;
        float pHeight = height / 8f;

        for (int i = 0; i <= 8; i++) {
            result.add(new CellBeanJava(i,
                    i % 3,
                    i / 3,
                    (i % 3 * 3 + 1) * pWidth,
                    (i / 3 * 3 + 1) * pHeight,
                    pWidth));
        }

//        Log.d("CellFactory", "result = $result");

        return result;
    }
}
