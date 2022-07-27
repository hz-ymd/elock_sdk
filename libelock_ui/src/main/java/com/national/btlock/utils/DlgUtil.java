package com.national.btlock.utils;

import android.content.Context;

import android.widget.Toast;



public class DlgUtil {


    public static void showToast(Context context, final String msg) {

        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastL(Context context, final String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }


}
