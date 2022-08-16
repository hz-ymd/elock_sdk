package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.national.btlock.adapter.LockPwdLongShareListAdapter;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DlgUtil;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.LockPwdShareListEntity;
import com.national.core.nw.it.OnResultListener;

import java.util.ArrayList;
import java.util.List;

public class LockPwdLongShareListActivity extends BaseActivity implements AppConstants {


    List<LockPwdShareListEntity.LockPwdShareData> mList;
    private ListView mListView;
    private LockPwdLongShareListAdapter mAdapter;


    String lockMac = "";
    String lockVer = "";
    String actionType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_national_pwd_auth_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoLock = (TextView) findViewById(R.id.id_lock_list_none_info);
        tvNoLock.setText(R.string.error_list_none);
//        lockType = LockType.LOCK_DELETE;
        if (getIntent() != null) {
            if (getIntent().getStringExtra("action_type") != null) {
                lockType = getIntent().getStringExtra("action_type");
            }


            if (getIntent().getStringExtra("lock_mac") != null) {
                lockMac = getIntent().getStringExtra("lock_mac");
            }
            if (getIntent().getStringExtra("lock_ver") != null) {
                lockVer = getIntent().getStringExtra("lock_ver");
            }
            if (getIntent().getStringExtra("action_type") != null) {
                actionType = getIntent().getStringExtra("action_type");
            }
            if (getIntent().getStringExtra("lock_auth_need_real_name") != null) {
                authIdcardNeedRealName = getIntent().getStringExtra("lock_auth_need_real_name");
            }

        }


        setTitle("访客码授权管理");


        initActivity();
        getAuthList();


    }

    @Override
    public void onNoDoubleClick(View v) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    String lockType;

    TextView tvNoLock;
//    ImageView imgvAddLock;

    private void initActivity() {
        mListView = (ListView) findViewById(R.id.id_serch_files_list);
        mListView.addHeaderView(new ViewStub(this));
        mList = new ArrayList<LockPwdShareListEntity.LockPwdShareData>();


        mAdapter = new LockPwdLongShareListAdapter(LockPwdLongShareListActivity.this, mList);
        mAdapter.setOnClickListener(new LockPwdLongShareListAdapter.IBtnOnClickListener() {
            @Override
            public void action(int btnType, final LockPwdShareListEntity.LockPwdShareData item) {
                Intent intent;

                if (btnType == 1) {
                    //解除授权

                    AlertDialog.Builder dlg = new AlertDialog.Builder(LockPwdLongShareListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    dlg.setMessage("确定删除？");
                    dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mItem = item;
                            deleteLockShare(item);
                        }
                    });

                    dlg.setNegativeButton("取消", null);
                    dlg.setCancelable(true);

                    if (!isFinishing()) {
                        dlg.create().show();
                    }
                } else if (btnType == 2) {
                    //密码再次分享
                    sendSysMsg(item.getTargetId(), item.getPwdShareStr());
                }
            }
        });

        mListView.setAdapter(mAdapter);

        updateList();

    }

    private void sendSysMsg(String targetTel, String msg) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        sendIntent.putExtra("address", targetTel); //电话号码，这行去掉的话，默认就没有电话

//        "【比特e锁】您的开锁密码是：1530-793-313，此密码在收到后的2小时内有效，且只能使用一次，在门锁按键输入“密码+#”后，便可开门，欢迎下载[e锁]手机APP。"
        sendIntent.putExtra("sms_body", msg);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

    String authIdcardNeedRealName = "0";

    private boolean isStrongSecurity() {
        return "1".equalsIgnoreCase(authIdcardNeedRealName);
    }

    private void getAuthList() {
        showProgressDialog();

        SDKCoreHelper.getLongLockPwdList(lockMac, new OnResultListener() {
            @Override
            public void onSuccess(String s) {
                dismissProgressDialog();
                LockPwdShareListEntity entity = new Gson().fromJson(s, LockPwdShareListEntity.class);
                if (entity != null && entity.getData() != null) {
                    // String currentUserId = loginEntity.getData().getUserId();
                    mList.clear();
                    if (null != entity.getData()) {
                        mList.addAll(entity.getData());
                    }
                    updateList();
                } else {
                    tvNoLock.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onError(String s, String s1) {
                dismissProgressDialog();
                DlgUtil.showToast(LockPwdLongShareListActivity.this, s1);
                tvNoLock.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);


            }
        });
    }

    private void deleteLockShare(LockPwdShareListEntity.LockPwdShareData item) {
        showProgressDialog();

        SDKCoreHelper.deleteVistorPwd(lockMac,item.getLockPwdId(), new OnResultListener() {
            @Override
            public void onSuccess(String s) {
                dismissProgressDialog();
                DlgUtil.showToast(LockPwdLongShareListActivity.this, "访客码删除成功");

                getAuthList();

                if (item.getType().equals("1")) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(LockPwdLongShareListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    dlg.setMessage("授权删除成功\n\n本次操作只更新了后台分享数据\n还需要和锁体完成数据同步\n");
                    dlg.setPositiveButton("数据同步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doSyncData();
                        }
                    });
                    dlg.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            LockPwdLongShareListActivity.this.finish();
                        }
                    });
                    dlg.setCancelable(true);
                    if (!isFinishing()) {
                        dlg.create().show();
                    }
                }
            }

            @Override
            public void onError(String s, String s1) {
                dismissProgressDialog();
                DlgUtil.showToast(LockPwdLongShareListActivity.this, s1);
            }
        });
//        model = new LoginModel(AuthListActivity.this, getRequestQueue(), AuthListActivity.this);
//        Map<String, String> requestParams = new HashMap<String, String>();
////        requestParams.put("lockId", lockId);
//        requestParams.put("lockPwdId", item.getLockPwdId());
//
//
//        model.doRequest(LoginModel.DELETE_LONG_LOCK_PWD_SHARE, requestParams, null, null, true, true);
    }


    LockPwdShareListEntity.LockPwdShareData mItem = null;


    private void doSyncData() {
        Intent intent = new Intent(LockPwdLongShareListActivity.this, BleComunicationInfoActivity.class);
        intent.putExtra("action_type", AppConstants.LockType.LOCK_SYNC_TIME);
        intent.putExtra("lockMac", lockMac);
        startActivity(intent);
    }


    private void updateList() {

        if (mList.size() == 0) {
            tvNoLock.setVisibility(View.VISIBLE);
        } else {
            tvNoLock.setVisibility(View.GONE);
        }
        mListView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }


}
