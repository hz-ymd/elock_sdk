package com.national.btlock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.national.btlock.utils.AppConstants;


public class OperateRecordActivity extends BaseActivity implements AppConstants {

    String lockMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_national_operate_record_main);
        setTitle(getString(R.string.txt_operate_record_list));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initActivity();

        if (getIntent().getExtras() != null) {
            lockMac = getIntent().getStringExtra("lockMac");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }


    private void initActivity() {

        findViewById(R.id.ll_me_lock_open_record_search).setOnClickListener(this);
        findViewById(R.id.ll_auth_record_search).setOnClickListener(this);
        findViewById(R.id.ll_tabs_record_search).setOnClickListener(this);


    }


    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_me_lock_open_record_search) {
            Intent intent = new Intent(OperateRecordActivity.this,
                    LockOpenRecordListActivity.class);
            intent.putExtra("lockMac", lockMac);
            startActivity(intent);
        } else if (id == R.id.ll_auth_record_search) {
            Intent intent = new Intent(OperateRecordActivity.this,
                    AuthRecordListActivity.class);
            intent.putExtra("lockMac", lockMac);
            startActivity(intent);
        } else if (id == R.id.ll_tabs_record_search) {
//            Intent intent = new Intent(OperateRecordActivity.this,
//                    TabDevicesMainActivity.class);
//            startActivity(intent);
        }

    }
}
