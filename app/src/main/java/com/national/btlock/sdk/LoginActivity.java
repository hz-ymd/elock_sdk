package com.national.btlock.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.national.btlock.sdk.model.LoginResult;
import com.national.btlock.sdk.utils.PreferencesUtils;

public class LoginActivity extends Activity implements Constants {
    EditText edit_username;
    EditText edit_password;
    Button btn_login;
    ProgressBar loading;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initActivity();
    }


    private void initActivity() {
        edit_username = findViewById(R.id.username);
        edit_password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login);
        loading = findViewById(R.id.loading);

        btn_login.setOnClickListener(view -> {
            String username = edit_username.getText().toString();
            String password = edit_password.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_LONG).show();
                return;
            }

            loading.setVisibility(View.VISIBLE);

            //第三方登录逻辑
            SdkHelper.getInstance().login(LoginActivity.this,username, new SdkHelper.CallBack() {
                @Override
                public void onSuccess(String jsonStr) {
                    loading.setVisibility(View.GONE);
                    Log.d(TAG, "jsonStr:" + jsonStr);
                    LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                    if (result != null) {
                        if (result.getData() != null) {
                            String isAccountVerified = result.getData().getAccountVerified();
                            PreferencesUtils.putBoolean(LoginActivity.this, IS_LOGIN, true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("isAccountVerified", isAccountVerified);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    loading.setVisibility(View.GONE);
                    Log.d(TAG, "error:" + errorCode + ",errorMsg:" + errorMsg);
                }
            });
        });


    }


}
