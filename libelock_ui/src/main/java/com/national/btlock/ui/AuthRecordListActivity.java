package com.national.btlock.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.national.btlock.adapter.AuthRecordListAdapter;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DateTimeUtil;
import com.national.btlock.utils.DlgUtil;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.btlock.widget.datepick.DatePickerDialog;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.AuthRecordListEntity;
import com.national.core.nw.it.OnResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AuthRecordListActivity extends BaseActivity implements AppConstants {
    private CustomDatePicker customDatePicker1, customDatePicker2;

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        Date endDateInit = DateTimeUtil.addDay(new Date(), -7);
        String endDate = TimeUtil.date2Str(endDateInit);


        Date start = DateTimeUtil.addYear(new Date(), -1);
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
        }, startStr, endStr);
        customDatePicker1.showSpecificTime(false);
        customDatePicker1.setIsLoop(true);

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {

                if (DateTimeUtil.dateCompare(tvStartDate.getText().toString(), time.split(" ")[0]) >= 0) {
                    tvEndDate.setText(time.split(" ")[0]);

                }
//                tvEndDate.setText(time.split(" ")[0]);
            }
        }, startStr, endStr);
        customDatePicker2.showSpecificTime(false);
        customDatePicker2.setIsLoop(true);
    }

    String lockMac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        setActionBarVisiblity(false);

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_national_auth_record_list);
        setTitle(getString(R.string.txt_auth_record_list));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            lockMac = getIntent().getStringExtra("lockMac");
        }


        initComponents();
//        intiPermission();

        initDatePicker();


        getRecordList();


    }

    static int page;

    private void testJvm(Person p, String va) {

        p.age = 30;
        va = "world";
        page = 100;


    }

    class Person {


        public String STR = "hello";
        public String name;
        public int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;

        }

    }


    class Person2 {


        public String STR = "hello2";
        public String name;
        public int age;

        Person2(String name, int age) {
            this.name = name;
            this.age = age;

        }

    }


    TextView tvStartDate;
    TextView tvEndDate;

    String authType = "0";
    String searchType = "0";
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

        mList = new ArrayList<AuthRecordListEntity.Record>();

        mListStored = new ArrayList<AuthRecordListEntity.Record>();


        mAdapter = new AuthRecordListAdapter(AuthRecordListActivity.this, mList);
        mListView.setAdapter(mAdapter);


        searchType = "0";
        tvLockNameFilter.setText(account[0]);


//        updateList();


    }

    List<AuthRecordListEntity.Record> mList;
    List<AuthRecordListEntity.Record> mListStored;
    private ListView mListView;
    private AuthRecordListAdapter mAdapter;

    private void getRecordList() {

        showProgressDialog();
        SDKCoreHelper.getLockAuthRecordList(lockMac, authType, searchType, tvStartDate.getText().toString(), tvEndDate.getText().toString(), new OnResultListener() {
            @Override
            public void onSuccess(String s) {
                dismissProgressDialog();
                AuthRecordListEntity entity = new Gson().fromJson(s, AuthRecordListEntity.class);
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
                DlgUtil.showToast(AuthRecordListActivity.this, s1);
                mList.clear();
                updateList();

            }
        });
    }

    private void updateList() {


        if (mList.size() == 0) {
            tvNoLock.setVisibility(View.VISIBLE);
        } else {
            tvNoLock.setVisibility(View.GONE);

            Collections.sort(mList, new Comparator<AuthRecordListEntity.Record>() {

                @Override
                public int compare(final AuthRecordListEntity.Record arg0, final AuthRecordListEntity.Record arg1) {
                    if (arg0 == null || TextUtils.isEmpty(arg0.getOperateTime())
                            || arg1 == null || TextUtils.isEmpty(arg1.getOperateTime())
                    ) {
                        return 0;
                    }
                    return arg1.getOperateTime().compareToIgnoreCase(arg0.getOperateTime());
                }
            });
        }


        mAdapter.notifyDataSetChanged();
    }


    TextView tvNoLock;


//    private void doLockShare() {
//        Map<String, String> requestParams = new HashMap<String, String>();
//        requestParams.put("userId", userId);
//        requestParams.put("startDate", tvStartDate.getText().toString());
//        requestParams.put("endDate", tvEndDate.getText().toString());
//
//
//        model.doRequest(LoginModel.LOCK_SHARE, requestParams, null, null, true, true);
//    }




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
            showSearchTypeSelect();
        } else if (id == R.id.id_open_method_filter) {
            showAuthTypeSelectDlg();
        }
    }


    private void showAuthTypeSelectDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);

        final String[] cities = {"所有", "APP", "身份证", "钥匙卡", "微信", "密码", "访客码", "权限转让", "权限归还", "指纹"};
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                authType = String.valueOf(which);
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

    final String[] account = {"我给他人授权", "他人授权给我"};

    private void showSearchTypeSelect() {

//        SystemClock.sleep(1000);


        AlertDialog.Builder builder = new AlertDialog.Builder(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);


        builder.setItems(account, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchType = String.valueOf(which);
                tvLockNameFilter.setText(account[which]);

                getRecordList();


            }
        });
        builder.show();

    }


    private int endDateYear;
    private int endDateMonth;
    private int endDateDay;

    private void showEndDatePickerDlg() {

        final DatePickerDialog datePickerDialogEnd = new DatePickerDialog(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT, null, endDateYear,
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

//        if (tvIdcardStartDate.getText().toString().contains("-")) {
//            datePickerDialogEnd.getDatePicker().setMinDate(TimeUtil.datePlus(tvIdcardStartDate.getText().toString(), +1).getTime());
////            datePickerDialogEnd.getDatePicker().setMinDate(TimeUtil.str2Date(tvIdcardStartDate.getText().toString()).getTime());
//        } else {
//            datePickerDialogEnd.getDatePicker().setMinDate((new Date()).getTime());
//        }


//        datePickerDialog.getDatePicker().setMinDate((new Date()).getTime());
        datePickerDialogEnd.setCanceledOnTouchOutside(true);
        datePickerDialogEnd.show();
    }


}
