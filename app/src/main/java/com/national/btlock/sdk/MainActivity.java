package com.national.btlock.sdk;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.national.btlock.sdk.databinding.ActivityMainBinding;
import com.national.btlock.sdk.utils.PreferencesUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements Constants {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Log.d(TAG, "MD5:" + getSignMd5Str().toUpperCase());
//        if (getIntent().getExtras() != null) {
//            String isAccountVerified = getIntent().getExtras().getString("isAccountVerified");
//            if (!TextUtils.isEmpty(isAccountVerified) && !isAccountVerified.equals("1")) {
//                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
//                dlg.setMessage("?????????????????????????????????");
//                dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Intent intent = new Intent(MainActivity.this, AccountVerifiedActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//                dlg.setNegativeButton("??????", null);
//                dlg.setCancelable(false);
//
//                if (!isFinishing()) {
//                    dlg.create().show();
//                }
//
////                SdkHelper.getInstance().identification(this, "name", "idcardNo",
////                        new SdkHelper.identificationCallBack() {
////                            @Override
////                            public void identificationSuc() {
////
////                            }
////
////                            @Override
////                            public void identificationError(String errCode, String errMsg) {
////
////                            }
////
////                        });
//            }
//        }

        SdkHelper.getInstance().loginListener(MainActivity.this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SdkHelper.getInstance().loginOut(new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        PreferencesUtils.putBoolean(MainActivity.this, Constants.IS_LOGIN, false);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errCode, String errMsg) {

                    }
                });
            }
        });


    }

    @Override
    protected void onResume() {
        String isAccountVerified = PreferencesUtils.getString(MainActivity.this, "isAccountVerified");
        if (!TextUtils.isEmpty(isAccountVerified) && !isAccountVerified.equals("1")) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            dlg.setMessage("?????????????????????????????????");
            dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(MainActivity.this, AccountVerifiedActivity.class);
                    startActivity(intent);
                }
            });

            dlg.setNegativeButton("??????", null);
            dlg.setCancelable(false);

            if (!isFinishing()) {
                dlg.create().show();
            }

//                SdkHelper.getInstance().identification(this, "name", "idcardNo",
//                        new SdkHelper.identificationCallBack() {
//                            @Override
//                            public void identificationSuc() {
//
//                            }
//
//                            @Override
//                            public void identificationError(String errCode, String errMsg) {
//
//                            }
//
//                        });
        }
        super.onResume();
    }

    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     * ????????????md5???
     *
     * @return ??????md5
     */
    public String getSignMd5Str() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}