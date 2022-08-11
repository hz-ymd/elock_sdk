package com.national.btlock.utils;

import com.national.sdkjni.HzbtJni;

public interface AppConstants {



    int MAX_RECONNECT_COUNT = 3;
    String ACTION_UPDATE_DEVICE_LIST_OPEN_LOCK = "com.national.update.device.list.lockopen";

    int SUCCESS = 1;

    String ACTION_SUCCESS = "1";
    String SEED = HzbtJni.getSeed();
    String APP_USER_INFO = "app_user_info";
    String LA = "Latitude";
    String LO = "Lontitude";
    String DECODE_MODE = "decode_mode";
    String USER_TYPE = "user_type";
    String ACTION_LOGIN_AGAIN = "com.national.login.again";


    boolean isDebug = true;


//    String SDK_URL = "http://test.hzbit.cn:60002/ymd/sdk-service/user";


    interface LockType {

        String LOCK_OPEN_BY_MAC = "lock_open_by_mac";

        String LOCK_PAY_DEPOSIT = "pay_deposit";
        String LOCK_PAY_SERVICE_FEE = "pay_service_fee";

        String LOCK_OPEN = "lock_open";
        String LOCK_SHARE = "lock_share";
        String LOCK_SHARE_WX = "lock_share_weixin";
        String LOCK_DELETE = "lock_delete";
        String LOCK_DELETE_BY_SELECT = "lock_delete_by_select";
        String LOCK_SYNC_TIME = "lock_sync_time";

        String LOCK_AUTH_CARD_A = "lock_auth_card_a";
        String LOCK_AUTH_IDCARD_NFC = "lock_auth_idcard_nfc";
        String LOCK_AUTH_IDCARD = "lock_auth_idcard";
        String LOCK_AUTH_IDCARD_BY_OCR = "lock_auth_idcard_by_ocr";

        String LOCK_VER_UPDATE = "lock_ver_update";
        String LOCK_VER_UPDATE_OUTLINES = "lock_ver_update_outlines";
        String LOCK_VER_UPDATE_LORA = "lock_ver_update_lora";
        String LOCK_PWD_SET = "lock_pwd_set";
        String LOCK_TMP_PWD_SHARE = "lock_tmp_pwd_share";//临时码分享
        String LOCK_LONG_PWD_SET = "lock_long_pwd_set";


        String GET_RECORD = "get_record";
    }

    interface LockPwdType {
        //00 管理员 01 普通密码 02 随身码 07应急码 03~06 预留
//        0:一次性密码 1：有效期密码
        String M = "0";
        String C = "1";
        String S = "2";
        String T = "7";
    }

    interface LockOwnerType {
        String O_M = "1";//ower,manager
        String O = "2";//owner
        String M = "3"; //manager
        String U = "4"; //common user
        String O_U = "5"; //owner with power of openning the door
        String V = "6";
    }

    interface ExtendType {
        String Managerment = "1";
        String AUTH_APP = "2";
        String AUTH_IDCARD = "3";
        String AUTH_CARD_A = "4";
        String AUTH_WX = "5";
        String AUTH_FP = "6";//指纹授权延长
    }

    String OLD_VERSION = "1.01,1.02,1.03,1.04,1.05,1.06,1.07,1.08,1.09,1.10,2.00,2.01,1.20,1.21,1.22,1.23,1.24";
    String OLD_VERSION_CONNECT_NOT_SUPPORT = "1.01,1.02,1.03,1.04,2.00"; //能够直接发起连接，但是连接上之后获取不到秘钥
}
