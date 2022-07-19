package com.national.btlock.model;

import android.text.TextUtils;

import java.io.Serializable;

public class AppItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getAppName() {
        if (TextUtils.isEmpty(appName)) {
            appName = shortName;
        }
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
        setShortName(appName);
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    String appName;
    int resId;


    int type; //0:新增 1:已设置
    String position; //列表显示顺序
    String shortName;
    String lockId;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLockId() {
        if (lockId == null) {
            lockId = "";
        }

        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public AppItem() {

    }

    public AppItem(String shortName1, int res) {
        type = 0;
        position = String.valueOf(System.currentTimeMillis());
        shortName = shortName1;
        setAppName(shortName);
        resId = res;
    }


}
