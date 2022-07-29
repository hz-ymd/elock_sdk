package com.national.btlock.widget;

import android.util.Log;
import android.view.View;

public abstract class NoDoubleListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static final String TAG = "NoDoubleListener";
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        } else {
            Log.w(TAG, "点击过快");
        }
    }

    public abstract void onNoDoubleClick(View v);
}

