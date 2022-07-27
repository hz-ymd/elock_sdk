package com.national.btlock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.idl.face.platform.utils.Base64Utils;
import com.national.btlock.face.ui.utils.IntentUtils;
import com.national.btlock.ocr.ui.camera.CameraActivity;
import com.national.btlock.ui.databinding.ActivityOpenDoorBinding;
import com.national.btlock.utils.AppConstants;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.it.OnProgressUpdateListener;
import com.national.core.nw.it.OnResultListener;

public class BleComunicationInfoActivity extends BaseActivity {
    private static final String TAG = "BleComunicationInfo";
    ActivityOpenDoorBinding binding;
    Animation animation;
    String lockName;
    String lockMac;
    String actionType;

    String targetUserId, startData, endData;

    private static final int REQUEST_IDCARD_FRONT = 777;
    String IMG_PATH_FRONT = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenDoorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            actionType = getIntent().getStringExtra("action_type");
            if (actionType.equals(AppConstants.LockType.LOCK_OPEN)) {
                setTitle("开门");
            } else if (actionType.equals(AppConstants.LockType.LOCK_AUTH_CARD_A)) {
                setTitle("钥匙卡授权");
                targetUserId = getIntent().getExtras().getString("targetUserId");
                startData = getIntent().getExtras().getString("startData");
                endData = getIntent().getExtras().getString("endData");
            } else if (actionType.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
                setTitle("身份证授权");
                targetUserId = getIntent().getExtras().getString("targetUserId");
                startData = getIntent().getExtras().getString("startData");
                endData = getIntent().getExtras().getString("endData");
            } else if (actionType.equals(AppConstants.LockType.LOCK_SYNC_TIME)) {
                setTitle("数据同步");
            }
            lockName = getIntent().getStringExtra("lockName");
            lockMac = getIntent().getStringExtra("lockMac");
        }
        animation = AnimationUtils.loadAnimation(BleComunicationInfoActivity.this, R.anim.exlore_line_move2);
        animation.setInterpolator(new LinearInterpolator());
        binding.imageView1.setAnimation(animation);
        binding.idLockName.setText(lockName);
        if (actionType.equals(AppConstants.LockType.LOCK_OPEN)) {
            openLock();
        } else if (actionType.equals(AppConstants.LockType.LOCK_AUTH_CARD_A)) {
            authCardA();
        } else if (actionType.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
            authIdCard();
        } else if (actionType.equals(actionType.equals(AppConstants.LockType.LOCK_SYNC_TIME))) {
            sycnData();
        }

    }

    private void authIdCard() {
        byte[] bytes = Base64Utils.decode(IntentUtils.getInstance().getBitmap(), Base64Utils.NO_WRAP);
        SDKCoreHelper.authIdcard(lockMac, targetUserId, startData, endData, bytes, new OnProgressUpdateListener() {
                    @Override
                    public void onProgressUpdate(String s) {
                        binding.idReceive.setText(s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(BleComunicationInfoActivity.this, "身份证授权成功", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(String s, String s1) {
//                        finish();
//                        Intent intent = new Intent(BleComunicationInfoActivity.this, OcrActivity.class);
//                        intent.putExtra("mac", lockMac);
//                        startActivity(intent);
                        binding.idReceive.setText("进入拍照识别模式，请根据提示操作");
                        Toast.makeText(BleComunicationInfoActivity.this, "进入拍照识别模式，请根据提示操作", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BleComunicationInfoActivity.this, CameraActivity.class);
                        intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, false);
                        IMG_PATH_FRONT = getExternalFilesDir(null).getAbsolutePath() + "idcardFront.jpg";
                        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, IMG_PATH_FRONT);
                        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                        startActivityForResult(intent, REQUEST_IDCARD_FRONT);
                    }
                }
        );
    }

    private void authIdCardByOcr(String filePath) {
        SDKCoreHelper.authIdcardByOcr(
                filePath, new OnResultListener() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "onSuccess:" + s);
                    }

                    @Override
                    public void onError(String s, String s1) {
                        Log.e(TAG, "onError:" + s1);
                    }
                });
    }

    private void authCardA() {
        SDKCoreHelper.authCardA(lockMac, targetUserId, startData, endData, new OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(String s) {
                binding.idReceive.setText(s);
            }

            @Override
            public void onSuccess(String s) {
                Toast.makeText(BleComunicationInfoActivity.this, "钥匙卡授权成功", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String s, String s1) {
                Toast.makeText(BleComunicationInfoActivity.this, "钥匙卡授权失败：" + s1, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openLock() {

        SDKCoreHelper.openLock(lockMac, new OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(String message) {
                binding.idReceive.setText(message);
            }

            @Override
            public void onSuccess(String jsonStr) {
                Toast.makeText(BleComunicationInfoActivity.this, "开门成功", Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(BleComunicationInfoActivity.this, "开门失败：" + errorMsg, Toast.LENGTH_LONG).show();
                finish();

            }
        });
    }


    private void sycnData() {
        SDKCoreHelper.syncData(lockMac, new OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(String s) {
                binding.idReceive.setText(s);
            }

            @Override
            public void onSuccess(String s) {
                Toast.makeText(BleComunicationInfoActivity.this, "数据同步成功", Toast.LENGTH_LONG).show();
                finish();

            }

            @Override
            public void onError(String s, String s1) {
                Toast.makeText(BleComunicationInfoActivity.this, "数据同步失败：" + s1, Toast.LENGTH_LONG).show();
                finish();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IDCARD_FRONT && resultCode == RESULT_OK) {
            authIdCardByOcr(IMG_PATH_FRONT);
        }

    }
}
