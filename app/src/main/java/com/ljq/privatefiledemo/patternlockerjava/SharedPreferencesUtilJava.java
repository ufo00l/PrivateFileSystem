package com.ljq.privatefiledemo.patternlockerjava;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtilJava {
    Context mContext = null;

    private SharedPreferences prefer = null;
    private SharedPreferences.Editor editor = null;

    public SharedPreferencesUtilJava(Context context) {
        mContext = context;
        prefer = context.getSharedPreferences("pattern", Context.MODE_PRIVATE);
        editor = prefer.edit();
    }

    void saveString(String name, String data) {
        editor.putString(name, data);
        editor.apply();
    }

    String getString(String name) {
        return prefer.getString(name, null);
    }
}
