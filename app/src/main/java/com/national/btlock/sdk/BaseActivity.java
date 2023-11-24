package com.national.btlock.sdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.national.btlock.sdk.model.LoginResult;
import com.national.btlock.sdk.utils.PreferencesUtils;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;
    TextView text_start;

    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_base);

        text_start = findViewById(R.id.text_start);

        text_start.setOnClickListener(view -> {
            pd = new ProgressDialog(BaseActivity.this);
            pd.show();
            String appid;
            String appSecret;

            if (PreferencesUtils.getInt(BaseActivity.this, "ORG", 0) == 0) {
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
                            login();
                        }

                        @Override
                        public void initFailure(String errCode, String errMsg) {
                            if (pd != null) {
                                pd.dismiss();
                                pd = null;
                            }
                            Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                        }

                    });
        });


    }


    public void login() {

        //第三方登录逻辑
        SdkHelper.getInstance().login(BaseActivity.this, PreferencesUtils.getString(BaseActivity.this, Constants.USER_NAME), new SdkHelper.CallBack() {
            @Override
            public void onSuccess(String jsonStr) {
                if (pd != null) {
                    pd.dismiss();
                    pd = null;
                }
                Log.d(TAG, "jsonStr:" + jsonStr);
                LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                if (result != null) {
                    if (result.getData() != null) {
                        String isAccountVerified = result.getData().getAccountVerified();
                        PreferencesUtils.putBoolean(BaseActivity.this, Constants.IS_LOGIN, true);
                        PreferencesUtils.putString(BaseActivity.this, "isAccountVerified", isAccountVerified);
                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                        startActivity(intent);
//                        finish();
                    }
                } else {
                    Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onError(String errorCode, String errorMsg) {
                if (pd != null) {
                    pd.dismiss();
                    pd = null;
                }
                if (errorCode.equals("6100011")) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(BaseActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    dlg.setMessage("该账号未实名，无法切换设备登录，请实名后重试");
                    dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            Intent intent = new Intent(BaseActivity.this, AccountVerifiedActivity.class);
                            startActivity(intent);
                        }
                    });

                    dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    dlg.setCancelable(false);

                    if (!isFinishing()) {
                        dlg.create().show();
                    }
                } else {
                    Toast.makeText(BaseActivity.this, "登录失败：" + errorMsg, Toast.LENGTH_LONG).show();
                }

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
