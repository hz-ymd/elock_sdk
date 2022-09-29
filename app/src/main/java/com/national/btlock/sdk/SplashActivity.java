package com.national.btlock.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.national.btlock.sdk.utils.PreferencesUtils;


public class SplashActivity extends Activity implements Constants {
    TextView text_start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        boolean is_login = PreferencesUtils.getBoolean(SplashActivity.this, IS_LOGIN, false);
        Intent intent;
        if (is_login) {
            intent = new Intent(SplashActivity.this, BaseActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();


    }

    private static final String TAG = "SplashActivity";

    private void init() {
        String appid;
        String appSecret;

        if (PreferencesUtils.getInt(SplashActivity.this, "ORG", 0) == 0) {
            appid = Constants.APPID;
            appSecret = Constants.APPSECRET;
        } else {
            appid = Constants.APPID1;
            appSecret = Constants.APPSECRET1;
        }

        SdkHelper.getInstance().init(getApplicationContext(),
                appid,
                appSecret,
//                "elock-sdk-demo-face-android",
//                "idl-license.face-android",
                new SdkHelper.initCallBack() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG, "initSuccess");
                        SdkHelper.getInstance().setBaiduFaceConfig(Constants.LICENSEID, Constants.LICENSEFILE_NAME);
                        init();
                    }

                    @Override
                    public void initFailure(String errCode, String errMsg) {
                        Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                    }

                });
    }
}
