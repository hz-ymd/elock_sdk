package com.national.btlock.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.national.btlock.sdk.model.LoginResult;
import com.national.btlock.sdk.utils.PreferencesUtils;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        //第三方登录逻辑
        SdkHelper.getInstance().login(BaseActivity.this, PreferencesUtils.getString(BaseActivity.this, Constants.USER_NAME), new SdkHelper.LoginCallBack() {
            @Override
            public void onSuccess(String jsonStr) {
                Log.d(TAG, "jsonStr:" + jsonStr);
                LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                if (result != null) {
                    if (result.getData() != null) {
                        String isAccountVerified = result.getData().getAccountVerified();
                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                        intent.putExtra("isAccountVerified", isAccountVerified);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFaceCheck() {
                SdkHelper.getInstance().faceLogin(BaseActivity.this, Constants.LICENSEID, Constants.LICENSEFILE_NAME, new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                        if (result != null) {
                            if (result.getData() != null) {
                                String isAccountVerified = result.getData().getAccountVerified();
                                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                                intent.putExtra("isAccountVerified", isAccountVerified);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String errCode, String errMsg) {

                    }
                });

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(BaseActivity.this, "登录失败：" + errorMsg, Toast.LENGTH_LONG).show();
            }
        });


//        SdkHelper.getInstance().init(BaseActivity.this, Constants.APPID, Constants.APPSECRET, new SdkHelper.initCallBack() {
//            @Override
//            public void initSuccess() {
//
//
//            }
//
//            @Override
//            public void initFailure(String errCode, String errMsg) {
//                Toast.makeText(BaseActivity.this, "初始化失败：" + errMsg, Toast.LENGTH_LONG).show();
//
//            }
//        });
    }
}
