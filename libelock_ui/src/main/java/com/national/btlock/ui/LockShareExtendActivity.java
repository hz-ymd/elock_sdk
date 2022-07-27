package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.national.btlock.ui.databinding.ActivityShareExtendBinding;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DlgUtil;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.CommonEntity;
import com.national.core.nw.it.OnResultListener;
import com.national.btlock.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LockShareExtendActivity extends BaseActivity {
    ActivityShareExtendBinding binding;
    String oldDateStr;
    private CustomDatePicker //customDatePicker1,
            customDatePicker2;

    TextView tvEndDate;

    String lockMac, extendType, user_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShareExtendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.auth_date_extend_txt));
        tvEndDate = binding.idIdcardDateValidPeriodInput;


        if (getIntent().getExtras() != null) {
            endTime = getIntent().getStringExtra("lock_auth_endtime");
            lockMac = getIntent().getStringExtra("lockMac");
            oldDateStr = getIntent().getStringExtra("end_date");
            extendType = getIntent().getStringExtra("extend_type");
            user_id = getIntent().getStringExtra("user_id");


            initDatePicker();


        }


        binding.idBtnExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChanged = false;
                Date start = DateTimeUtil.str2DateTime(oldDateStr);
                Date end = DateTimeUtil.str2DateTime(tvEndDate.getText().toString());

                if (end != null && start.getTime() != end.getTime()) {
                    isChanged = true;
                }

                if (isChanged) {
                    extendAuth();
                } else {
                    Toast.makeText(LockShareExtendActivity.this, "授权时间无变更", Toast.LENGTH_LONG).show();
                }
            }
        });


        binding.idIdcardDateValidPeriodInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker2.show(tvEndDate.getText().toString());
            }
        });


    }

    private void extendAuth() {
        showProgressDialog();
        SDKCoreHelper.extendAuth(lockMac, user_id, extendType, tvEndDate.getText().toString(), new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                CommonEntity commonEntity = new Gson().fromJson(jsonStr, CommonEntity.class);
                if (commonEntity != null) {
                    if (commonEntity.getStatus().equals("1") && commonEntity.getMessage().contains("数据同步")) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(LockShareExtendActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                        dlg.setMessage(commonEntity.getMessage());
                        dlg.setPositiveButton("数据同步", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                sycnData();
                            }
                        });
                        dlg.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        });
                        dlg.setCancelable(true);
                        if (!isFinishing()) {
                            dlg.create().show();
                        }
                    } else {
                        Toast.makeText(LockShareExtendActivity.this, "授权调整成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }


            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(LockShareExtendActivity.this, "授权调整失败：" + errorMsg, Toast.LENGTH_LONG).show();

            }
        });
    }


    String endTime = "";


    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        Date start = new Date();//DateTimeUtil.addYear(new Date(), -10);
        String startStr = sdf.format(start);
//        String endStr = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);

        Date endDateInit = DateTimeUtil.addDay(new Date(), 7);
        String endStr = TimeUtil.datetime2Str(endDateInit);

        if (endTime.contains("长期")) {
            endTime = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
        }

        if (!TextUtils.isEmpty(endTime)) {
            if (DateTimeUtil.datetimeCompare(endStr, endTime) >= 0) {
                endStr = endTime;
            }
        }
        tvEndDate.setText(oldDateStr);

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tvEndDate.setText(time);
            }
        }
                , startStr
                , endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

    public void sycnData() {
        Intent intent = new Intent(LockShareExtendActivity.this, BleComunicationInfoActivity.class);
        intent.putExtra("action_type", AppConstants.LockType.LOCK_SYNC_TIME);
        intent.putExtra("lockMac", lockMac);
        startActivity(intent);
    }
}
