package com.national.btlock.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetWorkUtil {
    public NetWorkUtil() {
    }

    public static Integer getNetworkState(Context context) {
        if (context == null) {
            return 0;
        } else {
            int netType = 0;
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                int nType = networkInfo.getType();
                if (nType == 1) {
                    netType = 1;
                } else if (nType == 0) {
                    int nSubType = networkInfo.getSubtype();
                    TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (nSubType == 13 && !telephonyManager.isNetworkRoaming()) {
                        netType = 4;
                    } else if (nSubType == 3 || nSubType == 5 || nSubType == 6 || nSubType == 8 || nSubType == 9 || nSubType == 10 || nSubType == 12 || nSubType == 14 || nSubType == 15 && !telephonyManager.isNetworkRoaming()) {
                        netType = 3;
                    } else if (nSubType != 1 && nSubType != 2 && nSubType != 4 && nSubType != 7 && (nSubType != 11 || telephonyManager.isNetworkRoaming())) {
                        netType = 0;
                    } else {
                        netType = 2;
                    }
                } else if (nType == 9) {
                    netType = 5;
                }

                return Integer.valueOf(netType);
            } else {
                return 0;
            }
        }
    }
}
