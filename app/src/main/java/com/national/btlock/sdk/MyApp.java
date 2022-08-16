package com.national.btlock.sdk;

import android.app.Application;
import android.util.Log;


public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         *  Constants.APPID sdk申请appId
         *  Constants.APPSECRET sdk申请appSecret
         *  licenseId 百度申请licenseId
         *  licenseFileName 百度配置文件名称
         */
        SdkHelper.getInstance().init(this,
                Constants.APPID,
                Constants.APPSECRET,
//                "elock-sdk-demo-face-android",
//                "idl-license.face-android",
                new SdkHelper.initCallBack() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG, "initSuccess");
                        SdkHelper.getInstance().setBaiduFaceConfig(Constants.LICENSEID, Constants.LICENSEFILE_NAME);
                    }

                    @Override
                    public void initFailure(String errCode, String errMsg) {
                        Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                    }

                });


    }
}
