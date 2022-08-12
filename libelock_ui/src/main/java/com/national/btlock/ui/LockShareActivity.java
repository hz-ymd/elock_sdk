package com.national.btlock.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.DensityUtils;
import com.national.btlock.sdk.SdkHelper;
import com.national.btlock.ui.databinding.ActivityLockShareBinding;
import com.national.btlock.ui.face.FaceLivenessExpActivity;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.NoDoubleListener;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.it.OnResultListener;
import com.national.btlock.utils.DateTimeUtil;
import com.national.btlock.face.ui.utils.IntentUtils;

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
    String authIdcardNeedRealName = "0";

    private static final int REQUEST_FACE = 5555;

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
        } else if (action_type.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
            binding.rlTitle.setVisibility(View.VISIBLE);
            chkSameEndTime.setVisibility(View.VISIBLE);
            setTitle("身份证授权");
            llAssignmentType.setVisibility(View.GONE);
            binding.idBtnRegister.setText(R.string.next);
            authIdcardNeedRealName = getIntent().getExtras().getString("authIdcardNeedRealName");
            if (authIdcardNeedRealName.equals("1")) {
                binding.llIdcardAliasInput.setVisibility(View.GONE);
                AlertDialog.Builder dlg = new AlertDialog.Builder(LockShareActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                dlg.setMessage("请被授权人本人进行刷脸操作");//确定解除绑定？
                dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        SdkHelper.getInstance().faceInit(LockShareActivity.this, new SdkHelper.CallBack() {
                            @Override
                            public void onSuccess(String jsonStr) {
                                Intent in = new Intent(LockShareActivity.this, FaceLivenessExpActivity.class);
                                in.putExtra("type", "authIdCard");
                                startActivityForResult(in, REQUEST_FACE);
                            }

                            @Override
                            public void onError(String errCode, String errMsg) {
                                Toast.makeText(LockShareActivity.this, "人脸识别初始化失败：" + errMsg, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
                dlg.setNegativeButton(getString(R.string.btn_next_time), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        LockShareActivity.this.finish();
                    }
                });

                dlg.setCancelable(false);
                if (!isFinishing()) {
                    dlg.create().show();
                }
            } else {
                binding.llIdcardAliasInput.setVisibility(View.VISIBLE);
                binding.idIdcardAliasInputTitle.setText(R.string.lock_share_idcard_alias_title);
                binding.idIdcardAliasInput.setHint(R.string.error_lock_share_idcard_alias_input_none);
            }
        }
//        initPermission();
    }

    @Override
    public void onNoDoubleClick(View v) {

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

            if (action_type.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
                if (authIdcardNeedRealName.equals("1") && TextUtils.isEmpty(bmpStr)) {
                    Toast.makeText(LockShareActivity.this, "请先采集人脸照片", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!authIdcardNeedRealName.equals("1") && TextUtils.isEmpty(targetUserId)) {
                    Toast.makeText(LockShareActivity.this, getString(R.string.error_lock_share_idcard_alias_input_none), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LockShareActivity.this, BleComunicationInfoActivity.class);
                intent.putExtra("action_type", action_type);
                intent.putExtra("lockMac", lockMac);
                intent.putExtra("startData", startData);
                intent.putExtra("endData", endData);
                intent.putExtra("targetUserId", targetUserId);
                intent.putExtra("authIdcardNeedRealName", authIdcardNeedRealName);
                startActivity(intent);
                finish();
                return;
            }

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

        tvStartDate.setOnClickListener(new NoDoubleListener() {
            @Override
            public void onNoDoubleClick(View view) {
                customDatePicker1.show(tvStartDate.getText().toString());
            }
        });


        tvEndDate.setOnClickListener(new NoDoubleListener() {
            @Override
            public void onNoDoubleClick(View view) {
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

        if (!isFinishing()) {
            dlg.create().show();
        }

    }

    String bmpStr;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_FACE) {
            if (resultCode == RESULT_OK) {
                bmpStr = IntentUtils.getInstance().getBitmap();
                Bitmap bmp = base64ToBitmap(bmpStr);
                bmp = FaceSDKManager.getInstance().scaleImage(bmp,
                        DensityUtils.dip2px(getApplicationContext(), 100),
                        DensityUtils.dip2px(getApplicationContext(), 100));

                binding.idAvaterImg.setImageBitmap(bmp);
            } else {
                finish();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public void initPermission() {
        boolean cameraSatePermission =
                ActivityCompat.checkSelfPermission(LockShareActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= 23
                && !cameraSatePermission) {
            requestPermission();
        } else {

        }
    }

    private static final int REQUEST_PERMISSION = 1000;

    String[] permsion = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    private void requestPermission() {
        ActivityCompat.requestPermissions(LockShareActivity.this
                , permsion
                , REQUEST_PERMISSION);

    }

    boolean isPermissionsNotGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == permsion.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isPermissionsNotGranted = true;

                        break;
                    }
                }
                if (isPermissionsNotGranted) {
                } else {
                }

            } else {
                Toast.makeText(getApplicationContext(), R.string.camera_permission_required, Toast.LENGTH_LONG).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
