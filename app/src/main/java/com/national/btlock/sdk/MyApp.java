package com.national.btlock.sdk;

import android.app.Application;
import android.util.Log;


public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        SdkHelper.getInstance().init(this, Constants.APPID, Constants.APPSECRET,
                "elock-sdk-demo-face-android",
                "idl-license.face-android",
                new SdkHelper.initCallBack() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG, "initSuccess");

                    }

                    @Override
                    public void initFailure(int errCode, String errMsg) {
                        Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                    }


                });


    }
}
