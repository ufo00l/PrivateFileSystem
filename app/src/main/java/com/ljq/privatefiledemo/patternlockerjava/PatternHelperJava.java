package com.ljq.privatefiledemo.patternlockerjava;

import android.content.Context;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PatternHelperJava {
    @Nullable
    private String message;
    private String storagePwd;
    private String tmpPwd;
    private int times;

    public boolean isFinish() {
        return isFinish;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isOk() {
        return isOk;
    }

    private boolean isFinish;
    private boolean isOk;
    @NotNull
    private SharedPreferencesUtilJava util;
    public static final int MIN_SIZE = 4;
    public static final int MAX_TIMES = 99;
    private static final String GESTURE_PWD_KEY = "gesture_pwd_key";

    public final void validateForSetting(@Nullable List hitIndexList) {
        this.isFinish = false;
        this.isOk = false;
        if (hitIndexList != null && hitIndexList.size() >= MIN_SIZE) {
            if (TextUtils.isEmpty((CharSequence) this.tmpPwd)) {
                this.tmpPwd = this.convert2String(hitIndexList);
                this.isOk = true;
            }
            else {
                if (this.tmpPwd.equals(this.convert2String(hitIndexList))) {
                    this.isOk = true;
                    this.isFinish = true;
                }

            }
        }
    }

    public final void confirm() {
        this.saveToStorage(this.tmpPwd);
    }

    public final void reset() {
        this.tmpPwd = (String) null;
        this.util.saveString(GESTURE_PWD_KEY, "");
    }

    public final void validateForChecking(@Nullable List hitIndexList) {
        this.isOk = false;
        if (hitIndexList != null && hitIndexList.size() >= MIN_SIZE) {
            this.storagePwd = this.util.getString(GESTURE_PWD_KEY);
            if (!TextUtils.isEmpty((CharSequence) this.storagePwd) &&
                    this.storagePwd.equals(this.convert2String(hitIndexList))) {
                this.isOk = true;
                this.isFinish = true;
            }
            else {
                this.times++;
                this.isFinish = this.times >= MIN_SIZE;
            }

        }
        else {
            this.times++;
            this.isFinish = this.times >= MIN_SIZE;
        }
    }

    private final int getRemainTimes() {
        return this.times < 5 ? MAX_TIMES - this.times : 0;
    }

    private final String convert2String(List hitIndexList) {
        return hitIndexList.toString();
    }

    private final void saveToStorage(String gesturePwd) {
        this.util.saveString(GESTURE_PWD_KEY, gesturePwd);
    }

    @Nullable
    public final String getPwdString() {
        return this.util.getString(GESTURE_PWD_KEY);
    }

    public PatternHelperJava(@NotNull Context context) {
        this.util = new SharedPreferencesUtilJava(context);
    }
}
