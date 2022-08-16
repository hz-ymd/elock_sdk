package com.national.btlock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.national.btlock.adapter.LockListAdapter;
import com.national.btlock.ui.databinding.ActivityNationalLockListBinding;
import com.national.btlock.widget.NoDoubleListener;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.entity.LockListEntity;
import com.national.core.nw.it.OnResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LockListActivity extends BaseActivity {

    private static final String TAG = "LockListActivity";

    ActivityNationalLockListBinding binding;
    List<LockListEntity.Lock> lockList;
    LockListAdapter adapter;

    View layout_no_lock;

    private static final int REQUEST_LOCK_DELETE = 11111;

    String key;

    private static final int REQUEST_SEARCH = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNationalLockListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        layout_no_lock = binding.layoutNoLock.getRoot();


        layout_no_lock.findViewById(R.id.btn_refresh).setOnClickListener(new NoDoubleListener() {
            @Override
            public void onNoDoubleClick(View view) {
                getLockList();
            }
        });
        lockList = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            key = getIntent().getExtras().getString("key");
        }

        binding.recyLock.setLayoutManager(new LinearLayoutManager(LockListActivity.this));

        adapter = new LockListAdapter(LockListActivity.this, lockList, new LockListAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                LockListEntity.Lock lock = lockList.get(position);
                String endTime = "长期";
                String str = lock.getValidPeriodStr();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String startTime = sdf.format(new Date());

                String[] time = str.split("至");
                if (time.length == 2) {
                    endTime = time[1];
                    startTime = time[0];
                }

                Intent intent = new Intent(LockListActivity.this, LockDetailActivity.class);
                intent.putExtra("lockMac", lock.getMac());
                intent.putExtra("lock_auth_endtime", endTime);
                intent.putExtra("ownerType", lock.getOwnerType());
                intent.putExtra("authIdcardNeedRealName", lock.getAuthIdcardNeedRealName());


                intent.putExtra("lock_name", lock.getLockName());
                intent.putExtra("lock_ver", lock.getLockVer());
                intent.putExtra("lock_hd_ver", lock.getHdVer());
                intent.putExtra("lock_mcu_ver", lock.getMcu());
                intent.putExtra("lock_auth_list_no", lock.getListNo());
                intent.putExtra("lock_type", lock.getLockType());
                intent.putExtra("lock_attribute", lock.getLockAttribute());
                intent.putExtra("lock_addr", lock.getAddress2());

                String lockType = lock.getLockType();

                String intHexString = "00" + lockType;
                int len = intHexString.length();
                String sTemp = intHexString.substring(len - 2, len);
                intent.putExtra("lock_hd_type", sTemp);
                startActivity(intent);
            }

            @Override
            public void open(int position) {
                LockListEntity.Lock lock = lockList.get(position);
                Intent intent = new Intent(LockListActivity.this, BleComunicationInfoActivity.class);
                intent.putExtra("action_type", AppConstants.LockType.LOCK_OPEN);
                intent.putExtra("lockMac", lock.getMac());
                intent.putExtra("lockName", lock.getLockName());
                startActivity(intent);


//                SDKCoreHelper.openLock(lock.getMac(), new OnProgressUpdateListener() {
//                    @Override
//                    public void onProgressUpdate(String message) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(String jsonStr) {
//
//                    }
//
//                    @Override
//                    public void onError(String errorCode, String errorMsg) {
//
//                    }
//                });
            }

            @Override
            public void assignment(int position) {
                LockListEntity.Lock lock = lockList.get(position);
                String endTime = "长期";
                String str = lock.getValidPeriodStr();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String startTime = sdf.format(new Date());

                String[] time = str.split("至");
                if (time.length == 2) {
                    endTime = time[1];
                    startTime = time[0];
                }
                Intent intent = new Intent(LockListActivity.this, LockShareActivity.class);
                intent.putExtra("action_type", AppConstants.LockType.LOCK_DELETE);
                intent.putExtra("lockMac", lock.getMac());
                intent.putExtra("lock_auth_endtime", endTime);
                intent.putExtra("ownerType", lock.getOwnerType());
                startActivityForResult(intent, REQUEST_LOCK_DELETE);
            }
        });
        binding.recyLock.setAdapter(adapter);

        binding.imageGrid.setOnClickListener(view -> finish());
//        binding.imageSearch.setOnClickListener(view -> {
//
//            Intent intent = new Intent(LockListActivity.this, SearchActivity.class);
//            startActivityForResult(intent, REQUEST_SEARCH);
//
//        });

        binding.imageRefresh.setOnClickListener(new NoDoubleListener() {
            @Override
            public void onNoDoubleClick(View view) {
                getLockList();
            }
        });

    }

    @Override
    public void onNoDoubleClick(View v) {

    }


    @Override
    protected void onResume() {
        getLockList();
        super.onResume();
    }


    private void getLockList(List<LockListEntity.Lock> allLock) {
        lockList.clear();
        List<LockListEntity.Lock> locks = new ArrayList<>();


        if (!TextUtils.isEmpty(key)) {
            if (allLock != null && allLock.size() != 0) {
                for (int i = 0; i < allLock.size(); i++) {
                    if (allLock.get(i).getLockName().contains(key)) {
                        locks.add(allLock.get(i));
                    }
                }
            }
        } else {
            locks.addAll(allLock);
        }
        if (locks != null && locks.size() != 0) {
            lockList.addAll(locks);
            adapter.notifyDataSetChanged();
            binding.recyLock.setVisibility(View.VISIBLE);
            layout_no_lock.setVisibility(View.GONE);
        } else {
            binding.recyLock.setVisibility(View.GONE);
            layout_no_lock.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOCK_DELETE && resultCode == RESULT_OK) {
            getLockList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getLockList() {
        showProgressDialog();
        SDKCoreHelper.getLockList(new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Log.d(TAG, jsonStr);
                LockListEntity lockListEntity = new Gson().fromJson(jsonStr, LockListEntity.class);
                if (lockListEntity.getData() != null && lockListEntity.getData().size() != 0) {
                    getLockList(lockListEntity.getData());
                } else {
                    binding.recyLock.setVisibility(View.GONE);
                    layout_no_lock.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(String errorCode, String errorMsg) {

//                binding.recyLock.setVisibility(View.GONE);
//                layout_no_lock.setVisibility(View.VISIBLE);
                SDKCoreHelper.getOffLineLockList(new OnResultListener() {
                    @Override
                    public void onSuccess(String s) {
                        dismissProgressDialog();
                        LockListEntity lockListEntity = new Gson().fromJson(s, LockListEntity.class);
                        if (lockListEntity.getData() != null && lockListEntity.getData().size() != 0) {
                            getLockList(lockListEntity.getData());
                        } else {
                            binding.recyLock.setVisibility(View.GONE);
                            layout_no_lock.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(String s, String s1) {
                        dismissProgressDialog();
                        binding.recyLock.setVisibility(View.GONE);
                        layout_no_lock.setVisibility(View.VISIBLE);

                    }
                });

                Log.d(TAG, errorCode + "," + errorMsg);
            }
        });
    }
}
