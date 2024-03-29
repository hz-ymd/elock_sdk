package com.national.btlock.ui.face;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.national.btlock.sdk.SdkHelper;
import com.national.btlock.ui.face.widget.TimeoutDialog;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.it.OnResultListener;
import com.national.btlock.face.ui.FaceLivenessActivity;
import com.national.btlock.face.ui.utils.IntentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceLivenessExpActivity extends FaceLivenessActivity implements
        TimeoutDialog.OnTimeoutDialogClickListener {

    private TimeoutDialog mTimeoutDialog;
    String name, idCardNo;
    String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加至销毁列表
        if (getIntent().getExtras() != null) {
            type = getIntent().getStringExtra("type");
            if (type.equals("identification")) {
                name = getIntent().getExtras().getString("name");
                idCardNo = getIntent().getExtras().getString("idCardNo");
            }
        }

    }

    @Override
    public void onLivenessCompletion(FaceStatusNewEnum status, String message,
                                     HashMap<String, ImageInfo> base64ImageCropMap,
                                     HashMap<String, ImageInfo> base64ImageSrcMap, int currentLivenessCount) {
        super.onLivenessCompletion(status, message, base64ImageCropMap, base64ImageSrcMap, currentLivenessCount);
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            // 获取最优图片
            getBestImage(base64ImageCropMap, base64ImageSrcMap);
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            if (mViewBg != null) {
                mViewBg.setVisibility(View.VISIBLE);
            }
            if (type.equals("identification") && SdkHelper.getInstance().getCallBack() != null) {
                SdkHelper.getInstance().getCallBack().identificationError("100003", "人脸采集超时");
            } else if (type.equals("loginFaceCheck") && SdkHelper.getInstance().getLoginCallBack() != null) {
                SdkHelper.getInstance().getLoginCallBack().onError("100003", "人脸采集超时");
            } else {
                Toast.makeText(FaceLivenessExpActivity.this, "人脸采集超时", Toast.LENGTH_SHORT).show();
            }
            finish();
            //showMessageDialog();
        }
    }


    /**
     * 获取最优图片
     *
     * @param imageCropMap 抠图集合
     * @param imageSrcMap  原图集合
     */
    private void getBestImage(HashMap<String, ImageInfo> imageCropMap, HashMap<String, ImageInfo> imageSrcMap) {
        String bmpStr = null;
        // 将抠图集合中的图片按照质量降序排序，最终选取质量最优的一张抠图图片
        if (imageCropMap != null && imageCropMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list1 = new ArrayList<>(imageCropMap.entrySet());
            Collections.sort(list1, new Comparator<Map.Entry<String, ImageInfo>>() {
                @Override
                public int compare(Map.Entry<String, ImageInfo> o1,
                                   Map.Entry<String, ImageInfo> o2) {
                    String[] key1 = o1.getKey().split("_");
                    String score1 = key1[2];
                    String[] key2 = o2.getKey().split("_");
                    String score2 = key2[2];
                    // 降序排序
                    return Float.valueOf(score2).compareTo(Float.valueOf(score1));
                }
            });

            // 获取抠图中的加密或非加密的base64
//            int secType = mFaceConfig.getSecType();
//            String base64;
//            if (secType == 0) {
//                base64 = list1.get(0).getValue().getBase64();
//            } else {
//                base64 = list1.get(0).getValue().getSecBase64();
//            }
        }

        // 将原图集合中的图片按照质量降序排序，最终选取质量最优的一张原图图片
        if (imageSrcMap != null && imageSrcMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list2 = new ArrayList<>(imageSrcMap.entrySet());
            Collections.sort(list2, new Comparator<Map.Entry<String, ImageInfo>>() {
                @Override
                public int compare(Map.Entry<String, ImageInfo> o1,
                                   Map.Entry<String, ImageInfo> o2) {
                    String[] key1 = o1.getKey().split("_");
                    String score1 = key1[2];
                    String[] key2 = o2.getKey().split("_");
                    String score2 = key2[2];
                    // 降序排序
                    return Float.valueOf(score2).compareTo(Float.valueOf(score1));
                }
            });
            bmpStr = list2.get(0).getValue().getBase64();

            // 获取原图中的加密或非加密的base64
//            int secType = mFaceConfig.getSecType();
//            String base64;
//            if (secType == 0) {
//                base64 = mBmpStr;
//            } else {
//                base64 = list2.get(0).getValue().getBase64();
//            }
        }

        // 页面跳转
//        IntentUtils.getInstance().setBitmap(bmpStr);
//        Intent intent = new Intent(FaceLivenessExpActivity.this,
//                CollectionSuccessActivity.class);
//        intent.putExtra("destroyType", "FaceLivenessExpActivity");
//        startActivity(intent);
//        finish();
        if (type.equals("identification")) {
            if (!TextUtils.isEmpty(bmpStr)) {
                byte[] bytes = Base64Utils.decode(bmpStr, Base64Utils.NO_WRAP);
                SDKCoreHelper.confirmPID(name, idCardNo, bytes, new OnResultListener() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        //Toast.makeText(FaceLivenessExpActivity.this, "实名认证成功", Toast.LENGTH_LONG).show();
                        SdkHelper.getInstance().getCallBack().identificationSuc();
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        //Toast.makeText(FaceLivenessExpActivity.this, "实名认证失败：" + errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "confirmPID onError errorCode=" + errorCode + " errorMsg=" + errorMsg);
                        SdkHelper.getInstance().getCallBack().identificationError(errorCode, errorMsg);
                    }
                });
            } else {
                SdkHelper.getInstance().getCallBack().identificationError("100002", "人脸采集失败");
            }
        } else if (type.equals("loginFaceCheck")) {
            if (!TextUtils.isEmpty(bmpStr)) {
                byte[] bytes = Base64Utils.decode(bmpStr, Base64Utils.NO_WRAP);
                SDKCoreHelper.loginFaceCheck(bytes, new OnResultListener() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        SdkHelper.getInstance().getLoginCallBack().onSuccess(jsonStr);
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Log.e(TAG, "loginFaceCheck=" + errorCode + " errorMsg=" + errorMsg);
                        SdkHelper.getInstance().getLoginCallBack().onError(errorCode, errorMsg);
                    }
                });
            } else {
                SdkHelper.getInstance().getLoginCallBack().onError("100002", "人脸采集失败");
            }

        } else if (type.equals("authIdCard")) {
            IntentUtils.getInstance().setBitmap(bmpStr);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        this.finish();
    }

    private void showMessageDialog() {
        mTimeoutDialog = new TimeoutDialog(this);
        mTimeoutDialog.setDialogListener(this);
        mTimeoutDialog.setCanceledOnTouchOutside(false);
        mTimeoutDialog.setCancelable(false);
        mTimeoutDialog.show();
        mHasShownTimeoutDialog = true;
        onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRecollect() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
            mHasShownTimeoutDialog = false;
        }
        if (mViewBg != null) {
            mViewBg.setVisibility(View.GONE);
        }
        onResume();
    }

    @Override
    public void onReturn() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
            mHasShownTimeoutDialog = false;
        }
        finish();
    }
}
