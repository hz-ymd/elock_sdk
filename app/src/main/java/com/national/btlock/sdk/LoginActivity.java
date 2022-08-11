package com.national.btlock.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

            PreferencesUtils.putBoolean(LoginActivity.this, IS_LOGIN, true);
            PreferencesUtils.putString(LoginActivity.this, USER_NAME, username);

            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
            startActivity(intent);

        });


    }


}
