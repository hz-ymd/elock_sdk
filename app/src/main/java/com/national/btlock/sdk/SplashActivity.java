package com.national.btlock.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.national.btlock.sdk.utils.PreferencesUtils;


public class SplashActivity extends Activity implements Constants {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean is_login = PreferencesUtils.getBoolean(SplashActivity.this, IS_LOGIN, false);
        Intent intent;
        if (is_login) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);


    }
}
