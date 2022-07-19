package com.national.btlock.ui;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.national.btlock.ui.databinding.ActivityOpenDoorBinding;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.it.OnProgressUpdateListener;

public class BleComunicationInfoActivity extends BaseActivity {

    ActivityOpenDoorBinding binding;
    Animation animation;
    String lockName;
    String lockMac;
    String actionType;

    String targetUserId, startData, endData;

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
        }


    }

    private void authCardA() {
        SDKCoreHelper.authCardA(lockMac, targetUserId, startData, endData, new OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(String s) {
                binding.idReceive.setText(s);
                if (s.equals("数据同步成功")) {
                    Toast.makeText(BleComunicationInfoActivity.this, "钥匙卡授权成功", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onSuccess(String s) {

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


}
