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
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();
    }

    public void init() {
//        if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                ActivityCompat.requestPermissions(BaseActivity.this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        PERMISSIONS_EXTERNAL_STORAGE);
//                return;
//            }
//        }
        //第三方登录逻辑
        SdkHelper.getInstance().login(BaseActivity.this, PreferencesUtils.getString(BaseActivity.this, Constants.USER_NAME), new SdkHelper.CallBack() {
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
