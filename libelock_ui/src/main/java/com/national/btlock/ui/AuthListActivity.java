package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.national.btlock.adapter.AuthListAdapter;
import com.national.btlock.ui.databinding.ActivityAuthListBinding;
import com.national.btlock.utils.DlgUtil;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.entity.CommonEntity;
import com.national.core.nw.entity.LockAuthListEntity;
import com.national.core.nw.it.OnResultListener;

import java.util.ArrayList;
import java.util.List;

public class AuthListActivity extends BaseActivity implements AppConstants {

    ActivityAuthListBinding binding;
    String lockMac;

    List<LockAuthListEntity.LockAuthItem> lockAuths;
    AuthListAdapter adapter;
    String actionType;
    String endTime;
    private static final int REQ_EXTEND = 5001;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            lockMac = getIntent().getStringExtra("lockMac");
            actionType = getIntent().getStringExtra("action_type");
            endTime = getIntent().getStringExtra("lock_auth_endtime");
        }

        lockAuths = new ArrayList<>();

        binding.recyAuth.setLayoutManager(new LinearLayoutManager(AuthListActivity.this));
        adapter = new AuthListAdapter(AuthListActivity.this, actionType, lockAuths, new AuthListAdapter.IAuthListBtnClickListener() {
            @Override
            public void action(int btnType, LockAuthListEntity.LockAuthItem item) {
                if (btnType == 1) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(AuthListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    dlg.setMessage("确定解除授权？");
                    dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (LockType.LOCK_SHARE.equalsIgnoreCase(actionType)
                                    || LockType.LOCK_SHARE_WX.equalsIgnoreCase(actionType)

                            ) {
                                deleteLockShare(item);
                                dialog.dismiss();
                            } else if (LockType.LOCK_AUTH_IDCARD.equalsIgnoreCase(actionType)
                                    || LockType.LOCK_AUTH_CARD_A.equalsIgnoreCase(actionType)
                                    || LockType.LOCK_AUTH_IDCARD_NFC.equalsIgnoreCase(actionType)

                            ) {
                                deleteAuthIdcard(item);
                                dialog.dismiss();
                            }

                        }
                    });
                    dlg.setNegativeButton("取消", null);
                    dlg.setCancelable(true);


                    if (!isFinishing()) {
                        dlg.create().show();
                    }
                }

                if (btnType == 3) {
                    Intent intent = new Intent(AuthListActivity.this, LockShareExtendActivity.class);

                    if (LockType.LOCK_SHARE.equalsIgnoreCase(actionType)) {
                        intent.putExtra("extend_type", ExtendType.AUTH_APP);
                        intent.putExtra("user_id", item.getTargetUserId());
                    } else if (LockType.LOCK_SHARE_WX.equalsIgnoreCase(actionType)) {
                        intent.putExtra("extend_type", ExtendType.AUTH_WX);
                        intent.putExtra("user_id", item.getTargetUserId());
                    } else if (LockType.LOCK_AUTH_IDCARD.equalsIgnoreCase(actionType)
                            || LockType.LOCK_AUTH_IDCARD_NFC.equalsIgnoreCase(actionType)
                    ) {
                        intent.putExtra("extend_type", ExtendType.AUTH_IDCARD);
                        intent.putExtra("user_id", item.getUid());
                    } else if (LockType.LOCK_AUTH_CARD_A.equalsIgnoreCase(actionType)) {
                        intent.putExtra("extend_type", ExtendType.AUTH_CARD_A);
                        intent.putExtra("user_id", item.getUid());
                    }
                    intent.putExtra("lockMac", lockMac);
                    intent.putExtra("lock_auth_endtime", endTime);
                    String endDate = item.getEndDate();
                    if (!TextUtils.isEmpty(endDate)) {
                        if (!endDate.contains(":")) {
                            endDate = endDate + " 12:00";
                        }

                        intent.putExtra("end_date", endDate);
                        startActivityForResult(intent, REQ_EXTEND);
                    }
                }

            }
        });
        binding.recyAuth.setAdapter(adapter);

        if (actionType.equals(LockType.LOCK_SHARE)) {
            setTitle(getString(R.string.app_share_manager));
            getAppAuthList();
        }
        if (actionType.equals(LockType.LOCK_AUTH_IDCARD)) {
            setTitle(getString(R.string.idcard_auth_manager));
            getIdCardAuth();
        }
        if (actionType.equals(LockType.LOCK_AUTH_CARD_A)) {
            setTitle(getString(R.string.card_auth_manager));
            getCardAAuth();
        }
    }

    @Override
    public void onNoDoubleClick(View v) {

    }


    public void dealAuth(String json) {
        LockAuthListEntity entity = new Gson().fromJson(json, LockAuthListEntity.class);
        if (entity != null && entity.getData() != null) {
            if (entity.getData().size() != 0) {
                binding.recyAuth.setVisibility(View.VISIBLE);
                binding.textNoAuth.setVisibility(View.GONE);
                lockAuths.addAll(entity.getData());
                adapter.notifyDataSetChanged();

            } else {
                binding.recyAuth.setVisibility(View.GONE);
                binding.textNoAuth.setVisibility(View.VISIBLE);
            }


        } else {
            binding.recyAuth.setVisibility(View.GONE);
            binding.textNoAuth.setVisibility(View.VISIBLE);
        }

    }

    public void dealError(String msg) {
        Toast.makeText(AuthListActivity.this, getString(R.string.auth_get_error) + msg, Toast.LENGTH_LONG).show();
        binding.recyAuth.setVisibility(View.GONE);
        binding.textNoAuth.setVisibility(View.VISIBLE);
    }


    public void getAppAuthList() {
        lockAuths.clear();
        SDKCoreHelper.authAppList(lockMac, new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dealAuth(jsonStr);


            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dealError(errorMsg);

            }
        });

    }


    public void getCardAAuth() {
        showProgressDialog();
        lockAuths.clear();
        SDKCoreHelper.authIdcardList(lockMac, "2", new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {

                dismissProgressDialog();
                dealAuth(jsonStr);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                dealError(errorMsg);
            }
        });
    }


    public void getIdCardAuth() {
        showProgressDialog();
        lockAuths.clear();
        SDKCoreHelper.authIdcardList(lockMac, "1", new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                dealAuth(jsonStr);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                dealError(errorMsg);
            }
        });
    }


    public void deleteLockShare(LockAuthListEntity.LockAuthItem item) {
        showProgressDialog();
        SDKCoreHelper.authAppDelete(item.getMac(), item.getTargetUserId(), new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Toast.makeText(AuthListActivity.this, getString(R.string.del_auth_suc), Toast.LENGTH_LONG).show();
                getAppAuthList();

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(AuthListActivity.this, getString(R.string.del_auth_error) + errorMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void deleteAuthIdcard(LockAuthListEntity.LockAuthItem item) {
        showProgressDialog();
        SDKCoreHelper.authIdcardDelete(item.getMac(), item.getUid(), item.getDnCode(), new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                CommonEntity commonEntity = new Gson().fromJson(jsonStr, CommonEntity.class);
                if (commonEntity != null) {
                    if (commonEntity.getStatus().equals("1") && commonEntity.getMessage().contains("数据同步")) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(AuthListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                        dlg.setMessage(commonEntity.getMessage());
                        dlg.setPositiveButton("数据同步", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                sycnData();
                            }
                        });
                        dlg.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        dlg.setCancelable(true);
                        if (!isFinishing()) {
                            dlg.create().show();
                        }
                    } else {
                        DlgUtil.showToast(AuthListActivity.this, commonEntity.getMessage());
                        finish();
                    }
                }


                if (actionType.equals(LockType.LOCK_AUTH_IDCARD)) {
                    getIdCardAuth();
                }
                if (actionType.equals(LockType.LOCK_AUTH_CARD_A)) {
                    getCardAAuth();
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(AuthListActivity.this, getString(R.string.del_auth_error) + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXTEND && resultCode == RESULT_OK) {
            if (actionType.equals(LockType.LOCK_SHARE)) {
                getAppAuthList();
            }
            if (actionType.equals(LockType.LOCK_AUTH_IDCARD)) {
                getIdCardAuth();
            }
            if (actionType.equals(LockType.LOCK_AUTH_CARD_A)) {
                getCardAAuth();
            }
        }
    }

    public void sycnData() {
        Intent intent = new Intent(AuthListActivity.this, BleComunicationInfoActivity.class);
        intent.putExtra("action_type", AppConstants.LockType.LOCK_SYNC_TIME);
        intent.putExtra("lockMac", lockMac);
        startActivity(intent);
    }
}
