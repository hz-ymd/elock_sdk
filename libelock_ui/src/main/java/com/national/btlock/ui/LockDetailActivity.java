package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.national.btlock.adapter.FunctionGridAdapter;
import com.national.btlock.model.AppItem;
import com.national.btlock.ui.databinding.ActivityLockDetailBinding;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.DeviceDetailEntity;
import com.national.core.nw.it.OnResultListener;
import com.national.btlock.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;


public class LockDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_LOCK_DELETE = 11111;
    private static final int REQUEST_LOCK_AUTH = 22222;
    private static final int REQUEST_LOCK_SHARE = 33333;
    private static final int REQ_EXTEND = 44444;
    private static final int REQUEST_LONG_PWD_SET = 55555;
    ActivityLockDetailBinding binding;
    GridView grid_func;
    List<AppItem> mList;
    FunctionGridAdapter adapter;
    LinearLayout content;
    TextView auth_app, auth_idcard, auth_card, auth_pwd;
    TextView text_owner, text_manager, text_manager_time;
    LinearLayout layout_auth;
    RelativeLayout layout_owner_manager;
    TextView assignment_take_back, assignment_extend, assignment_give_back;

    String endTime;
    String lockMac;
    String ownerType;
    String authIdcardNeedRealName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLockDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("设备详情");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            endTime = getIntent().getStringExtra("lock_auth_endtime");
            lockMac = getIntent().getStringExtra("lockMac");
            ownerType = getIntent().getStringExtra("ownerType");
            authIdcardNeedRealName = getIntent().getStringExtra("authIdcardNeedRealName");
        }

        initView();
        getLockDetail();


    }


    private void initView() {
        grid_func = findViewById(R.id.grid_func);
        layout_auth = findViewById(R.id.layout_auth);
        auth_app = findViewById(R.id.auth_app);
        auth_idcard = findViewById(R.id.auth_idcard);
        auth_card = findViewById(R.id.auth_card);
        auth_pwd = findViewById(R.id.auth_pwd);
        text_owner = findViewById(R.id.text_owner);
        text_manager = findViewById(R.id.text_manager);
        text_manager_time = findViewById(R.id.text_manager_time);


        assignment_give_back = findViewById(R.id.assignment_give_back);
        assignment_take_back = findViewById(R.id.assignment_take_back);
        assignment_extend = findViewById(R.id.assignment_extend);


        content = findViewById(R.id.content);
        layout_owner_manager = findViewById(R.id.layout_owner_manager);
        layout_owner_manager.setOnClickListener(this);

        findViewById(R.id.assignment_extend).setOnClickListener(this);

        findViewById(R.id.assignment_give_back).setOnClickListener(this);
        findViewById(R.id.assignment_take_back).setOnClickListener(this);
        mList = new ArrayList<>();
        adapter = new FunctionGridAdapter(LockDetailActivity.this, mList);
        grid_func.setAdapter(adapter);// 调用ImageAdapter

        auth_app.setOnClickListener(this);
        auth_idcard.setOnClickListener(this);
        auth_card.setOnClickListener(this);
        auth_pwd.setOnClickListener(this);
        initAppItem();
    }

    int actionType;


    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        if (id == R.id.assignment_take_back) {
            //removeFlg 0:不删除 1:删除
            actionType = 2;
            LayoutInflater inflater = LayoutInflater.from(LockDetailActivity.this);
            View layout = inflater.inflate(R.layout.layout_dialog_chkbox, null);
            chkBoxDeleteAll = (CheckBox) layout.findViewById(R.id.chk_manager_give_back);//注意这一句
            chkBoxDeleteAll.setChecked(true);
            show2ndConfirmDlg(layout, getString(R.string.auth_get_back_txt));

        } else if (id == R.id.assignment_give_back) {
            actionType = 1;
            LayoutInflater inflater = LayoutInflater.from(LockDetailActivity.this);
            View layout = inflater.inflate(R.layout.layout_dialog_chkbox, null);
            chkBoxDeleteAll = (CheckBox) layout.findViewById(R.id.chk_manager_give_back);//注意这一句
            chkBoxDeleteAll.setChecked(true);
            show2ndConfirmDlg(layout, getString(R.string.auth_give_back_management_txt));

        } else if (id == R.id.assignment_extend) {
            //管理权授权调整
            Intent intent = new Intent(LockDetailActivity.this, LockShareExtendActivity.class);
            intent.putExtra("extend_type", AppConstants.ExtendType.Managerment);
            intent.putExtra("user_id", deviceDetailEntity.getData().getManagerName());
            intent.putExtra("lockMac", lockMac);
            intent.putExtra("lock_auth_endtime", endTime);
            String endDate = deviceDetailEntity.getData().getEndDate();
            if (!TextUtils.isEmpty(endDate)) {
                if (!endDate.contains(":")) {
                    endDate = endDate + " 12:00";
                }
                intent.putExtra("end_date", endDate);
                startActivityForResult(intent, REQ_EXTEND);
            }

        } else if (id == R.id.auth_app) {
            goAuthList(lockMac, AppConstants.LockType.LOCK_SHARE);
        } else if (id == R.id.auth_idcard) {
            goAuthList(lockMac, AppConstants.LockType.LOCK_AUTH_IDCARD);
        } else if (id == R.id.auth_card) {
            goAuthList(lockMac, AppConstants.LockType.LOCK_AUTH_CARD_A);
        } else if (id == R.id.auth_pwd) {

            Intent intent = new Intent(LockDetailActivity.this, LockPwdLongShareListActivity.class);
            intent.putExtra("lock_mac", lockMac);
            startActivity(intent);


        }
    }

    public void goAuthList(String lockMac, String actionType) {
        Intent intent = new Intent(LockDetailActivity.this, AuthListActivity.class);
        intent.putExtra("lockMac", lockMac);
        intent.putExtra("lock_auth_endtime", endTime);
        intent.putExtra("action_type", actionType);
        startActivityForResult(intent, REQUEST_LOCK_AUTH);

    }

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public void initAppItem() {

        AppItem item = null;
        item = new AppItem();
        item.setAppName(getString(R.string.share_user));
        item.setResId(R.drawable.icon_lock_share);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_card));
        item.setResId(R.drawable.icon_card_a);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_pwd));
        item.setResId(R.drawable.icon_auth_visitor);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_idcard));
        item.setResId(R.drawable.icon_auth_idcard);
        mList.add(item);

//        item = new AppItem();
//        item.setAppName(getString(R.string.device_manager));
//        item.setResId(R.drawable.icon_auth_tmp);
//        mList.add(item);


        item = new AppItem();
        item.setAppName(getString(R.string.device_data));
        item.setResId(R.drawable.icon_user_guiders);
        mList.add(item);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();

        grid_func.setOnItemClickListener((adapterView, view, i, l) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
            } else {
                return;
            }
            switch (mList.get(i).getAppName()) {
                case "用户授权":
                    goNext(AppConstants.LockType.LOCK_SHARE);
                    break;
                case "钥匙卡授权":
                    goNext(AppConstants.LockType.LOCK_AUTH_CARD_A);
                    break;
                case "身份证授权":
                    goNext(AppConstants.LockType.LOCK_AUTH_IDCARD);
                    break;
                case "访客码授权":
                    goNext(AppConstants.LockType.LOCK_LONG_PWD_SET);
                    break;
                case "数据查询":
                    goNext(AppConstants.LockType.GET_RECORD);
                    break;
                default:
                    Toast.makeText(LockDetailActivity.this, "开发中，敬请期待", Toast.LENGTH_SHORT).show();
                    break;
//                case "更多功能":
//                    break;
            }

        });
    }


    public void goNext(String actionType) {


        if (actionType.equals(AppConstants.LockType.LOCK_SHARE) || actionType.equals(AppConstants.LockType.LOCK_AUTH_CARD_A)) {
            if (ownerType.equals(AppConstants.LockOwnerType.M) || ownerType.equals(AppConstants.LockOwnerType.O_M)) {
            } else {
                Toast.makeText(LockDetailActivity.this, "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (actionType.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
            if (ownerType.equals(AppConstants.LockOwnerType.O)) {
                Toast.makeText(LockDetailActivity.this, "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }

            if (ownerType.equals(AppConstants.LockOwnerType.O_U) && ownerType.equals(AppConstants.LockOwnerType.U)) {
                if (authIdcardNeedRealName.equals("0")) {
                    Toast.makeText(LockDetailActivity.this, "您没有权限", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (actionType.equals(AppConstants.LockType.LOCK_LONG_PWD_SET)) {
            if (ownerType.equals(AppConstants.LockOwnerType.O)) {
                Toast.makeText(LockDetailActivity.this, "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }
        }


        Intent intent;
        if (actionType.equals(AppConstants.LockType.LOCK_LONG_PWD_SET)) {
            intent = new Intent(LockDetailActivity.this, LockPwdShareActivity.class);
        } else if (actionType.equals(AppConstants.LockType.GET_RECORD)) {
            intent = new Intent(LockDetailActivity.this, OperateRecordActivity.class);
        } else {
            intent = new Intent(LockDetailActivity.this, LockShareActivity.class);
        }

        intent.putExtra("action_type", actionType);
        intent.putExtra("lockMac", lockMac);
        intent.putExtra("lock_auth_endtime", endTime);
        intent.putExtra("ownerType", ownerType);
        intent.putExtra("authIdcardNeedRealName", authIdcardNeedRealName);
        if (actionType.equals(AppConstants.LockType.LOCK_SHARE)) {
            startActivityForResult(intent, REQUEST_LOCK_SHARE);
        }
        if (actionType.equals(AppConstants.LockType.LOCK_DELETE)) {
            startActivityForResult(intent, REQUEST_LOCK_DELETE);
        }
        if (actionType.equals(AppConstants.LockType.LOCK_AUTH_CARD_A) || actionType.equals(AppConstants.LockType.LOCK_AUTH_IDCARD)) {
            startActivityForResult(intent, REQUEST_LOCK_SHARE);
        }

        if (actionType.equals(AppConstants.LockType.LOCK_LONG_PWD_SET)) {
            startActivityForResult(intent, REQUEST_LONG_PWD_SET);
        }

        if (actionType.equals(AppConstants.LockType.GET_RECORD)) {
            startActivity(intent);
        }


    }

    CheckBox chkBoxDeleteAll;

    private void show2ndConfirmDlg(View layout, final String pwdTitle) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(LockDetailActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        if (layout != null) {
            dlg.setView(layout);
        }
        dlg.setTitle(pwdTitle);
        dlg.setPositiveButton(pwdTitle, (dialog, whichButton) -> {
            giveBackManagement(pwdTitle);
            dialog.dismiss();

        });
        dlg.setNegativeButton("取消", (dialog, whichButton) -> dialog.dismiss());
        dlg.setCancelable(true);

        if (!isFinishing()) {
            dlg.create().show();
        }

    }

    private void giveBackManagement(String msg) {
        SDKCoreHelper.giveBackManagement(lockMac, chkBoxDeleteAll.isChecked() ? "1" : "0", new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {

                Toast.makeText(LockDetailActivity.this, msg + "成功", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(LockDetailActivity.this, msg + "失败：" + errorMsg, Toast.LENGTH_LONG).show();

            }
        });


    }


    DeviceDetailEntity deviceDetailEntity;

    public void getLockDetail() {
        showProgressDialog();
        layout_auth.setVisibility(View.GONE);
        layout_owner_manager.setVisibility(View.GONE);
        SDKCoreHelper.getLockDetails(lockMac, new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {

                dismissProgressDialog();
                deviceDetailEntity = new Gson().fromJson(jsonStr, DeviceDetailEntity.class);
                if (deviceDetailEntity != null) {

                    if (ownerType.equals(AppConstants.LockOwnerType.O_M) || ownerType.equals(AppConstants.LockOwnerType.M)) {
                        layout_auth.setVisibility(View.VISIBLE);
                        auth_app.setText("APP授权人数：" + deviceDetailEntity.getData().getAuthAppCount());
                        auth_idcard.setText("身份证授权人数：" + deviceDetailEntity.getData().getAuthIdcardCount());
                        auth_card.setText("钥匙卡授权人数：" + deviceDetailEntity.getData().getAuthCardACount());
                        auth_pwd.setText("访客码授权人数：" + deviceDetailEntity.getData().getAuthVistorPwdCount());
                    } else {
                        layout_auth.setVisibility(View.GONE);

                    }
                    layout_owner_manager.setVisibility(View.VISIBLE);
                    text_owner.setText("所有权：" + deviceDetailEntity.getData().getOwnerName());
                    text_manager.setText("管理权：" + deviceDetailEntity.getData().getManagerName());
                    text_manager_time.setText("有效期：" + deviceDetailEntity.getData().getDateStr());


                    assignment_take_back.setVisibility(View.GONE);
                    assignment_extend.setVisibility(View.GONE);
                    assignment_give_back.setVisibility(View.GONE);

                    if (ownerType.equals(AppConstants.LockOwnerType.O) || ownerType.equals(AppConstants.LockOwnerType.O_U)) {
                        assignment_take_back.setVisibility(View.VISIBLE);
                        assignment_extend.setVisibility(View.VISIBLE);
                    }

                    if (ownerType.equals(AppConstants.LockOwnerType.O_M)) {
                    }

                    if (ownerType.equals(AppConstants.LockOwnerType.M)) {
                        assignment_give_back.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(LockDetailActivity.this, "获取详情失败" + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOCK_DELETE && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode == REQUEST_LOCK_SHARE && resultCode == RESULT_OK) {
            getLockDetail();
        }
        if (requestCode == REQ_EXTEND && resultCode == RESULT_OK) {
            getLockDetail();
        }
        if (requestCode == REQUEST_LONG_PWD_SET) {
            getLockDetail();
        }
        if (requestCode == REQUEST_LOCK_AUTH) {
            getLockDetail();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
