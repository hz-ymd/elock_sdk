package com.national.btlock.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.national.btlock.sdk.utils.PreferencesUtils;

public class LoginActivity extends Activity implements Constants {
    EditText edit_username;
    EditText edit_password;
    Button btn_login;
    ProgressBar loading;
    private static final String TAG = "LoginActivity";
    Spinner spinner_org;

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

        spinner_org = findViewById(R.id.spinner_org);
        String[] orgs = new String[]{"组织1", "组织2"};


        spinner_org.setAdapter(new ArrayAdapter(LoginActivity.this,
                android.R.layout.simple_list_item_1, orgs));

        spinner_org.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferencesUtils.putInt(LoginActivity.this, "ORG", i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_login.setOnClickListener(view -> {
            String username = edit_username.getText().toString();
            String password = edit_password.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_LONG).show();
                return;
            }
            if (!username.startsWith("1") || username.length() != 11) {
                Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
                return;
            }

            loading.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
//            PreferencesUtils.putBoolean(LoginActivity.this, IS_LOGIN, true);
            PreferencesUtils.putString(LoginActivity.this, USER_NAME, username);
            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();


        });


    }


}
