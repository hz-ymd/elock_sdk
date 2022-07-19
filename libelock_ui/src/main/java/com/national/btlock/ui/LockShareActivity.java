package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.national.btlock.ui.databinding.ActivityLockShareBinding;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.it.OnResultListener;
import com.national.btlock.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LockShareActivity extends BaseActivity {
    ActivityLockShareBinding binding;
    String lockMac;
    String targetUserId;
    String startData, endData;

    TextView tvStartDate;
    TextView tvEndDate;
    CheckBox chkSameEndTime;
    String endTime;
    String action_type;
    String ownerType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLockShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        if (getIntent().getExtras() != null) {
            action_type = getIntent().getExtras().getString("action_type");
            ownerType = getIntent().getExtras().getString("ownerType");
            endTime = getIntent().getStringExtra("lock_auth_endtime");
            if (!TextUtils.isEmpty(endTime) && endTime.equals("长期")) {
                endTime = getMaxDateText();
            }
            lockMac = getIntent().getExtras().getString("lockMac");
        }

        if (action_type.equals(AppConstants.LockType.LOCK_SHARE)) {
            chkSameEndTime.setVisibility(View.VISIBLE);
            setTitle("用户授权");
            llAssignmentType.setVisibility(View.GONE);
        } else if (action_type.equals(AppConstants.LockType.LOCK_DELETE)) {
            chkSameEndTime.setVisibility(View.GONE);
            setTitle("权限转让");
            llAssignmentType.setVisibility(View.VISIBLE);
            if (ownerType.equals(AppConstants.LockOwnerType.O) || ownerType.equals(AppConstants.LockOwnerType.O_U)) {
                binding.chkOwer.setVisibility(View.VISIBLE);
                binding.chkManager.setVisibility(View.GONE);
                binding.chkOwer.setChecked(true);
                binding.chkOwer.setEnabled(false);
                binding.chkManager.setChecked(false);
            }

            if (ownerType.equals(AppConstants.LockOwnerType.O_M)) {
                binding.chkOwer.setVisibility(View.VISIBLE);
                binding.chkManager.setVisibility(View.VISIBLE);
                binding.chkManager.setChecked(true);
                binding.chkManager.setEnabled(false);

            }
        } else if (action_type.equals(AppConstants.LockType.LOCK_AUTH_CARD_A)) {
            chkSameEndTime.setVisibility(View.VISIBLE);
            setTitle("钥匙卡授权");
            llAssignmentType.setVisibility(View.GONE);
            binding.idIdcardAliasInputTitle.setText(R.string.lock_share_idcard_alias_title);
            binding.idIdcardAliasInput.setHint(R.string.error_lock_share_idcard_alias_input_none);
        }
    }

    LinearLayout llOwnerValidPeriod, llAssignmentType;

    public void initView() {
        llOwnerValidPeriod = binding.llAuthDateValidPeriodInput;
        llOwnerValidPeriod.setVisibility(View.GONE);
        llAssignmentType = binding.llAssignmentType;

        tvStartDate = binding.idIdcardStartDateInput;
        tvEndDate = binding.idIdcardDateValidPeriodInput;

        chkSameEndTime = binding.chkSameEndTime;

        chkSameEndTime.setOnCheckedChangeListener(mOnCheckedChangeListener);

        binding.chkOwer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llOwnerValidPeriod.setVisibility(View.VISIBLE);
                    chkSameEndTime.setVisibility(View.GONE);
                    binding.llIdcardStartDateInput.setVisibility(View.GONE);
                    binding.llIdcardDateValidPeriodInput.setVisibility(View.GONE);
                    binding.view21.setVisibility(View.GONE);
                    binding.view11.setVisibility(View.GONE);
                } else {
                    llOwnerValidPeriod.setVisibility(View.GONE);
                    chkSameEndTime.setVisibility(View.VISIBLE);
                    binding.llIdcardStartDateInput.setVisibility(View.VISIBLE);
                    binding.llIdcardDateValidPeriodInput.setVisibility(View.VISIBLE);
                    binding.view21.setVisibility(View.VISIBLE);
                    binding.view11.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.idBtnRegister.setOnClickListener(view -> {
            targetUserId = binding.idIdcardAliasInput.getText().toString();
            startData = tvStartDate.getText().toString();
            endData = tvEndDate.getText().toString();

            if (TextUtils.isEmpty(targetUserId)) {
                if (action_type.equals(AppConstants.LockType.LOCK_SHARE)) {
                    Toast.makeText(LockShareActivity.this, "请输入账号", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LockShareActivity.this, getString(R.string.error_lock_share_idcard_alias_input_none), Toast.LENGTH_LONG).show();
                }
                return;
            }

            if (action_type.equals(AppConstants.LockType.LOCK_SHARE)) {
                authApp();
            }
            if (action_type.equals(AppConstants.LockType.LOCK_DELETE)) {
                show2ndConfirmDlg();
            }

            if (action_type.equals(AppConstants.LockType.LOCK_AUTH_CARD_A)) {
                Intent intent = new Intent(LockShareActivity.this, BleComunicationInfoActivity.class);
                intent.putExtra("action_type", action_type);
                intent.putExtra("lockMac", lockMac);
                intent.putExtra("startData", startData);
                intent.putExtra("endData", endData);
                intent.putExtra("targetUserId", targetUserId);
                startActivity(intent);
                finish();
            }

        });


        initDatePicker();

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker1.show(tvStartDate.getText().toString());
            }
        });


        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker2.show(tvEndDate.getText().toString());

            }
        });

    }


    private void authManagement() {
        String type;
        if (binding.chkManager.isChecked() && binding.chkOwer.isChecked()) {
            type = "1";

        } else if (binding.chkManager.isChecked() && !binding.chkOwer.isChecked()) {
            type = "3";
        } else {
            type = "2";
        }

        if (!type.equals("3")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            startData = sdf.format(new Date());
            endData = getMaxDateText();
        }
        showProgressDialog();
        SDKCoreHelper.authManagement(lockMac, type, targetUserId, startData, endData, new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Toast.makeText(LockShareActivity.this, "权限转让成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(LockShareActivity.this, "权限转让失败：" + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void authApp() {
        showProgressDialog();
        SDKCoreHelper.authApp(lockMac, targetUserId, startData, endData, "", new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Toast.makeText(LockShareActivity.this, "授权成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();

                Toast.makeText(LockShareActivity.this, "授权失败：" + errorMsg, Toast.LENGTH_LONG).show();

            }
        });
    }


    private String getDefaultEndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date endDateInit = DateTimeUtil.addDay(new Date(), 7);
        String endDate = TimeUtil.date2Str(endDateInit);
        endDate = endDate + " 12:00";
        return endDate;
    }


    CustomDatePicker customDatePicker1, customDatePicker2;


    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        tvStartDate.setText(now);

        Date start = new Date();
        String startStr = sdf.format(start);

        String endStr = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);

        if (!TextUtils.isEmpty(endTime)) {
            endStr = endTime;
        }


        tvEndDate.setText(getDefaultEndDate());

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                if (DateTimeUtil.datetimeCompare(time, tvEndDate.getText().toString()) >= 0) {
                    tvStartDate.setText(time);
                }
            }
        }, startStr, endStr);
        customDatePicker1.showSpecificTime(true); // 不显示时和分
        customDatePicker1.setIsLoop(true); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间

                if (DateTimeUtil.datetimeCompare(tvStartDate.getText().toString(), time) >= 0) {

                    chkSameEndTime.setOnCheckedChangeListener(null);
                    chkSameEndTime.setChecked(false);
                    tvEndDate.setText(time);

                    chkSameEndTime.setOnCheckedChangeListener(mOnCheckedChangeListener);

                }
            }
        }, startStr, endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }


    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (!TextUtils.isEmpty(endTime) && endTime.contains("长期")) {
                    tvEndDate.setText(getMaxDateText());
                } else {

                    tvEndDate.setText(endTime);
                }


            } else {
                tvEndDate.setText(getDefaultEndDate());
            }
        }
    };


    private String getMaxDateText() {
        return DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
    }

    private void show2ndConfirmDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(LockShareActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        dlg.setMessage("确定进行权限转让？");
        dlg.setTitle("提示");
        dlg.setPositiveButton("确定", (dialog, whichButton) -> {
            dialog.dismiss();
            authManagement();
        });
        dlg.setNegativeButton("取消", (dialog, whichButton) -> dialog.dismiss());
        dlg.setCancelable(true);

        if (!isFinishing())
            dlg.create().show();

    }
}
