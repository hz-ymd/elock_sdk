package com.national.btlock.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.national.btlock.adapter.OpenRecordListAdapter02;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DateTimeUtil;
import com.national.btlock.utils.DlgUtil;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.btlock.widget.datepick.DatePickerDialog;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.OpenRecordListEntity;
import com.national.core.nw.it.OnResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class LockOpenRecordListActivity extends BaseActivity implements AppConstants, OnClickListener {
    String userId = "";
    private CustomDatePicker customDatePicker1, customDatePicker2;

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        Date endDateInit = DateTimeUtil.addDay(new Date(), -2);
        String endDate = TimeUtil.date2Str(endDateInit);

        Date start = DateTimeUtil.addYear(new Date(), -10);
        String startStr = sdf.format(start);
//        Date end = DateTimeUtil.addYear(new Date(), 10);
//        String endStr = sdf.format(end);
        String endStr = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
        tvStartDate.setText(endDate);
        tvEndDate.setText(now.split(" ")[0]);
//        tvEndDate.setText(endDate);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                if (DateTimeUtil.dateCompare(time.split(" ")[0], tvEndDate.getText().toString()) >= 0) {
                    tvStartDate.setText(time.split(" ")[0]);

                }
            }
        }, startStr, endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(true); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间

                if (DateTimeUtil.dateCompare(tvStartDate.getText().toString(), time.split(" ")[0]) >= 0) {
                    tvEndDate.setText(time.split(" ")[0]);
                }
//                tvEndDate.setText(time.split(" ")[0]);
            }
        }, startStr, endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(false); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

    String lockMac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        setActionBarVisiblity(false);

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_national_lock_open_record_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.txt_lock_open_record_list));

        if (getIntent().getExtras() != null) {
            lockMac = getIntent().getStringExtra("lockMac");
        }


        initComponents();
//        intiPermission();

        initDatePicker();


        getRecordList();
    }


    TextView tvStartDate;
    TextView tvEndDate;
    //    RadioButton rbAll;
//    RadioButton rbApp;
//    RadioButton rbIdcard;
    String radioSelect = "0";
    TextView tvLockNameFilter;
    TextView tvOpenMethodFilter;

    private void initComponents() {
        tvStartDate = (TextView) findViewById(R.id.id_open_record_start_date);
        tvEndDate = (TextView) findViewById(R.id.id_open_record_end_date);

        tvLockNameFilter = (TextView) findViewById(R.id.id_lock_name_filter);
        tvLockNameFilter.setOnClickListener(this);
        tvOpenMethodFilter = (TextView) findViewById(R.id.id_open_method_filter);
        tvOpenMethodFilter.setOnClickListener(this);


        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        map = new HashMap<String, String>();

        tvNoLock = (TextView) findViewById(R.id.id_lock_list_none_info);
        ((TextView) findViewById(R.id.id_btn_open_record_search)).setOnClickListener(this);


        mListView = (ListView) findViewById(R.id.id_lock_open_record_list);
        mListView.addHeaderView(new ViewStub(this));

        mList = new ArrayList<OpenRecordListEntity.OpenRecord>();

        mListStored = new ArrayList<OpenRecordListEntity.OpenRecord>();
//        OpenRecordListEntity.OpenRecord record = new OpenRecordListEntity.OpenRecord();
//        record.setUserName("15857100308");
//        record.setOpenTime("2017-12-21 10:48:39");
//        record.setOpenMethod("蓝牙开门");
//        mList.add(record);
//
//        record = new OpenRecordListEntity.OpenRecord();
//        record.setUserName("18757139274");
//        record.setOpenTime("2017-12-21 10:48:28");
//        record.setOpenMethod("身份证开门");
//        mList.add(record);


        mAdapter = new OpenRecordListAdapter02(LockOpenRecordListActivity.this, mList);
        mListView.setAdapter(mAdapter);

//        updateList();


    }

    List<OpenRecordListEntity.OpenRecord> mList;
    List<OpenRecordListEntity.OpenRecord> mListStored;
    private ListView mListView;
    private OpenRecordListAdapter02 mAdapter;

    private void getRecordList() {
        showProgressDialog();
        SDKCoreHelper.getLockOpenRecordList(lockMac, radioSelect, tvStartDate.getText().toString() + " 00:00", tvEndDate.getText().toString() + " 23:59", new OnResultListener() {
            @Override
            public void onSuccess(String s) {
                dismissProgressDialog();
                OpenRecordListEntity entity = new Gson().fromJson(s, OpenRecordListEntity.class);
                mList.clear();
                mListStored.clear();
                if (null != entity.getData()) {
                    mListStored.addAll(entity.getData());
                    mList.addAll(entity.getData());
                }
                updateList();
            }

            @Override
            public void onError(String s, String s1) {
                dismissProgressDialog();
                DlgUtil.showToast(LockOpenRecordListActivity.this, s1);
                mList.clear();
                updateList();

            }
        });
    }

    private void updateList() {
//        mList.clear();
//        String filter = tvLockNameFilter.getText().toString();
//        if (getString(R.string.txt_lock_name_filter_default).equalsIgnoreCase(filter)) {
//            mList.addAll(mListStored);
//
//        } else {
////            List<OpenRecordListEntity.OpenRecord> list = new ArrayList<OpenRecordListEntity.OpenRecord>();
//
//            for (OpenRecordListEntity.OpenRecord record : mListStored) {
//                if (record.getLockName().equalsIgnoreCase(filter)) {
//                    mList.add(record);
//                }
//            }
//        }

        if (mList.size() == 0) {
            tvNoLock.setVisibility(View.VISIBLE);
        } else {
            tvNoLock.setVisibility(View.GONE);

            Collections.sort(mList, new Comparator<OpenRecordListEntity.OpenRecord>() {

                @Override
                public int compare(final OpenRecordListEntity.OpenRecord arg0, final OpenRecordListEntity.OpenRecord arg1) {
                    if (arg0 == null || TextUtils.isEmpty(arg0.getOpenTime())
                            || arg1 == null || TextUtils.isEmpty(arg1.getOpenTime())
                    ) {
                        return 0;
                    }
                    return arg1.getOpenTime().compareToIgnoreCase(arg0.getOpenTime());
                }
            });
        }


        mAdapter.notifyDataSetChanged();
    }


    TextView tvNoLock;




    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        if (id == R.id.id_btn_open_record_search) {
            getRecordList();
        } else if (id == R.id.id_open_record_start_date) {
            customDatePicker1.show(tvStartDate.getText().toString());
        } else if (id == R.id.id_open_record_end_date) {
            customDatePicker2.show(tvEndDate.getText().toString());
        } else if (id == R.id.id_lock_name_filter) {
            showAccountSelect();
        } else if (id == R.id.id_open_method_filter) {
            showMethodSelectDlg();
        }

    }


    private void showMethodSelectDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LockOpenRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);

        final String[] cities = {"所有", "APP", "身份证", "钥匙卡", "微信", "密码"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                radioSelect = String.valueOf(which);
                tvOpenMethodFilter.setText(cities[which]);
                getRecordList();
//                System.out.println("radioSelect="+radioSelect);
            }
        });

        if (!isFinishing()) {
            builder.show();
        }


    }

    HashMap<String, String> map;

    ////    String data;
    private void showAccountSelect() {
//
        String data = SDKCoreHelper.getLockNames();
//
        if (TextUtils.isEmpty(data)) {
            DlgUtil.showToast(LockOpenRecordListActivity.this, getString(R.string.error_lock_names_none));
            return;
        }
//
        final String[] account = data.split(",");

        AlertDialog.Builder builder = new AlertDialog.Builder(LockOpenRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);

        builder.setItems(account, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "选择的城市为：" + cities[which], Toast.LENGTH_SHORT).show();

//                userId = map.get(array[which]);
//                if (!StringUtil.isEmpty(userId)) {
                tvLockNameFilter.setText(account[which]);

                updateList();
//                }


            }
        });
        builder.show();
//
//
    }


    private int endDateYear;
    private int endDateMonth;
    private int endDateDay;

    private void showEndDatePickerDlg() {

        final DatePickerDialog datePickerDialogEnd = new DatePickerDialog(LockOpenRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT, null, endDateYear,
                endDateMonth, endDateDay, "结束时间选择");

        datePickerDialogEnd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel), (DialogInterface.OnClickListener) null);

        datePickerDialogEnd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            @SuppressLint("NewApi")
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // Do Stuff
                    endDateYear = datePickerDialogEnd.getDatePicker().getYear();
                    endDateMonth = datePickerDialogEnd.getDatePicker().getMonth();
                    endDateDay = datePickerDialogEnd.getDatePicker().getDayOfMonth();

                }
            }
        });


        datePickerDialogEnd.setCanceledOnTouchOutside(true);
        datePickerDialogEnd.show();
    }


}
