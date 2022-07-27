package com.national.btlock.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DateTimeUtil;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.btlock.widget.datepick.DatePickerDialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AuthRecordListActivity
//        extends BaseActivity implements Callback, AppConstants, OnClickListener
{
//    TextView tvRegister;
//    String userId = "";
//    private CustomDatePicker customDatePicker1, customDatePicker2;
//
//    private void initDatePicker() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
//        String now = sdf.format(new Date());
//
//
//        Date endDateInit = DateTimeUtil.addDay(new Date(), -7);
//        String endDate = TimeUtil.date2Str(endDateInit);
//
//
//        Date start = DateTimeUtil.addYear(new Date(), -1);
//        String startStr = sdf.format(start);
//
////        Date end = DateTimeUtil.addYear(new Date(), 10);
////        String endStr = sdf.format(end);
//        String endStr = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
//        tvStartDate.setText(endDate);
//        tvEndDate.setText(now.split(" ")[0]);
////        tvEndDate.setText(endDate);
//
//        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) { // 回调接口，获得选中的时间
//
//                if (DateTimeUtil.dateCompare(time.split(" ")[0], tvEndDate.getText().toString()) >= 0) {
//                    tvStartDate.setText(time.split(" ")[0]);
//
//                }
//
//
//            }
//        }, startStr, endStr);
//        customDatePicker1.showSpecificTime(false);
//        customDatePicker1.setIsLoop(true);
//
//        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) {
//
//                if (DateTimeUtil.dateCompare(tvStartDate.getText().toString(), time.split(" ")[0]) >= 0) {
//                    tvEndDate.setText(time.split(" ")[0]);
//
//                }
////                tvEndDate.setText(time.split(" ")[0]);
//            }
//        }, startStr, endStr);
//        customDatePicker2.showSpecificTime(false);
//        customDatePicker2.setIsLoop(true);
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
////        setActionBarVisiblity(false);
//
//        super.onCreate(savedInstanceState);
//        this.setContentView(R.layout.acitivity_auth_record_list);
//        setTitle(getString(R.string.txt_auth_record_list));
//
//
//        initComponents();
////        intiPermission();
//
//
//        initDatePicker();
//
//
//        getRecordList();
//
//
////        HandlerThread thread = new HandlerThread("");
////        thread.start();
////
////        thread.quit();
////        test();
//
//
////        new Thread() {
////            @Override
////            public void run() {
////
////                ArrayList<String> localFilesPath = new ArrayList<String>();
////                getAllFiles(localFilesPath, ROOT_PATH);
////
////                try {
////                    for (String fileDeta : localFilesPath) {
////
////                        if (!TextUtils.isEmpty(fileDeta)) {
////                            test1(fileDeta);
////                        }
////                    }
////                } catch (IOException e) {
////                    e.printStackTrace();
////
////                } catch (NoSuchAlgorithmException e1) {
////                    e1.printStackTrace();
////                }
////
////
////            }
////        }.start();
//
////        testEvnet();
//
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                testEvent();
////            }
////        }).start();
//
//
////
////        Person p = new Person("hh", 20);
////        String va = "hello";
////        page =1;
////        testJvm(p, va);
////        MyLog.e(TAG, "va=" + va + " p.name=" + p.name + " p.age=" + p.age + " page="+page);
////
////        Person2 p2 = new Person2("hh", 20);
////        MyLog.e(TAG,"p.STR==p2.STR:"+String.valueOf(p.STR==p2.STR));
////
////        MyLog.e(TAG,"getFtpIp:"+    JniUtil.getFtpIp());
//
//
//    }
//
//    static int page;
//    private void testJvm(Person p, String va) {
//
//        p.age = 30;
//        va = "world";
//        page =100;
//
//
//    }
//
//    class Person {
//
//
//        public  String STR = "hello";
//        public String name;
//        public int age;
//
//        Person(String name, int age) {
//            this.name = name;
//            this.age = age;
//
//        }
//
//    }
//
//
//    class Person2 {
//
//
//        public  String STR = "hello2";
//        public String name;
//        public int age;
//
//        Person2(String name, int age) {
//            this.name = name;
//            this.age = age;
//
//        }
//
//    }
//
//
//
//
//
//
//
//
//
//
//
//    final static int PAGE_ = 10;
//
//
//
//
//
//    TextView tvStartDate;
//    TextView tvEndDate;
//    //    RadioButton rbAll;
////    RadioButton rbApp;
////    RadioButton rbIdcard;
//    String authType = "0";
//    String searchType = "0";
//    TextView tvLockNameFilter;
//    TextView tvOpenMethodFilter;
//
//    private void initComponents() {
//        tvStartDate = (TextView) findViewById(R.id.id_open_record_start_date);
//        tvEndDate = (TextView) findViewById(R.id.id_open_record_end_date);
//
//        tvLockNameFilter = (TextView) findViewById(R.id.id_lock_name_filter);
//        tvLockNameFilter.setOnClickListener(this);
//        tvOpenMethodFilter = (TextView) findViewById(R.id.id_open_method_filter);
//        tvOpenMethodFilter.setOnClickListener(this);
//
//
//        tvStartDate.setOnClickListener(this);
//        tvEndDate.setOnClickListener(this);
//
//        map = new HashMap<String, String>();
//
//        tvNoLock = (TextView) findViewById(R.id.id_lock_list_none_info);
//        ((TextView) findViewById(R.id.id_btn_open_record_search)).setOnClickListener(this);
//
//
//        mListView = (ListView) findViewById(R.id.id_lock_open_record_list);
//        mListView.addHeaderView(new ViewStub(this));
//
//        mList = new ArrayList<AuthRecordListEntity.Record>();
//
//        mListStored = new ArrayList<AuthRecordListEntity.Record>();
//
//
//        mAdapter = new AuthRecordListAdapter(AuthRecordListActivity.this, mList);
//        mListView.setAdapter(mAdapter);
//
//
//        searchType = "0";
//        tvLockNameFilter.setText(account[0]);
//
//
////        updateList();
//
//
//    }
//
//    List<AuthRecordListEntity.Record> mList;
//    List<AuthRecordListEntity.Record> mListStored;
//    private ListView mListView;
//    private AuthRecordListAdapter mAdapter;
//
//    private void getRecordList() {
//
//        Map<String, String> requestParams = new HashMap<String, String>();
//        requestParams.put("userId", userId);
//        requestParams.put("startDate", tvStartDate.getText().toString());
//        requestParams.put("endDate", tvEndDate.getText().toString());
//        requestParams.put("authType", authType);//radioSelect
//        requestParams.put("searchType", searchType);
//
//        model.doRequest(LoginModel.GET_AUTH_RECORD, requestParams, null, null, true, true);
//    }
//
//    private void updateList() {
//
//        if (mList.size() == 0) {
//            tvNoLock.setVisibility(View.VISIBLE);
//        } else {
//            tvNoLock.setVisibility(View.GONE);
//
//            Collections.sort(mList, new Comparator<AuthRecordListEntity.Record>() {
//
//                @Override
//                public int compare(final AuthRecordListEntity.Record arg0, final AuthRecordListEntity.Record arg1) {
//                    if (arg0 == null || TextUtils.isEmpty(arg0.getOperateTime())
//                            || arg1 == null || TextUtils.isEmpty(arg1.getOperateTime())
//                    ) {
//                        return 0;
//                    }
//                    return arg1.getOperateTime().compareToIgnoreCase(arg0.getOperateTime());
//                }
//            });
//        }
//
//
//        mAdapter.notifyDataSetChanged();
//    }
//
//
////    private void initData(){
////        StringBuilder builder = new StringBuilder();
////
////        for(AuthRecordListEntity.Record record:mList){
//////            if(data.contains(record.getLockName())){
//////                continue;
//////            }
////            if(builder.lastIndexOf(record.getLockName())>0){
////                continue;
////            }
////            builder.append(record.getLockName()+",");
////
////        }
////        data= builder.toString();
////    }
//
//    TextView tvNoLock;
//
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        switch (msg.what) {
//            case LoginModel.GET_AUTH_RECORD: {
//                model.dismissProgressDialog();
//                int success = msg.arg1;
//                if (success == SUCCESS) {
//                    AuthRecordListEntity entity = (AuthRecordListEntity) msg.obj;
//                    String status = entity.getStatus();
//                    if (ACTION_SUCCESS.equalsIgnoreCase(status)) {
//                        mList.clear();
//                        mListStored.clear();
//                        if (null != entity.getData()) {
//                            mListStored.addAll(entity.getData());
//
//                            mList.addAll(entity.getData());
//                        }
//
//                        updateList();
//
//
//                    }
//                } else {
//                    String message = (String) msg.obj;
//                    if (!TextUtils.isEmpty(message)) {
////					 showPrompt(this, TYPE_TOAST, message, null);
//                        DlgUtil.showToast(AuthRecordListActivity.this, message);
//                        mList.clear();
//                        updateList();
//                    }
//                    // startLogin();
//                }
//            }
//
//
//            default:
//                break;
//        }
//        return false;
//    }
//
////    private void doLockShare() {
////        Map<String, String> requestParams = new HashMap<String, String>();
////        requestParams.put("userId", userId);
////        requestParams.put("startDate", tvStartDate.getText().toString());
////        requestParams.put("endDate", tvEndDate.getText().toString());
////
////
////        model.doRequest(LoginModel.LOCK_SHARE, requestParams, null, null, true, true);
////    }
//
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
////        Map<String, String> requestParams = new HashMap<String, String>();
//
//        switch (v.getId()) {
//
//            case R.id.id_btn_open_record_search:
//                getRecordList();
//
//                break;
//            case R.id.id_open_record_start_date:
//                customDatePicker1.show(tvStartDate.getText().toString());
//                break;
//            case R.id.id_open_record_end_date:
//                customDatePicker2.show(tvEndDate.getText().toString());
//                break;
//
//            case R.id.id_lock_name_filter:
//
//                showSearchTypeSelect();
//                break;
//            case R.id.id_open_method_filter:
//
//                showAuthTypeSelectDlg();
//                break;
//
//
//            default:
//                break;
//        }
//
//    }
//
//
//
//
//    private void showAuthTypeSelectDlg() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
//
//        final String[] cities = {"所有", "APP", "身份证", "钥匙卡", "微信", "密码", "访客码","权限转让","权限归还", "指纹"};
//        //    设置一个下拉的列表选择项
//        builder.setItems(cities, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                authType = String.valueOf(which);
//                tvOpenMethodFilter.setText(cities[which]);
//                getRecordList();
////                System.out.println("radioSelect="+radioSelect);
//            }
//        });
//
//        if (!isFinishing())
//            builder.show();
//
//
//    }
//
//    HashMap<String, String> map;
//
//    final String[] account = {"我给他人授权", "他人授权给我"};
//
//    private void showSearchTypeSelect() {
//
////        SystemClock.sleep(1000);
//
//
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT);
//
//
//        builder.setItems(account, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                searchType = String.valueOf(which);
//                tvLockNameFilter.setText(account[which]);
//
//                getRecordList();
//
//
//            }
//        });
//        builder.show();
//
//    }
//
//
//    private int endDateYear;
//    private int endDateMonth;
//    private int endDateDay;
//
//    private void showEndDatePickerDlg() {
//
//        final DatePickerDialog datePickerDialogEnd = new DatePickerDialog(AuthRecordListActivity.this, AlertDialog.THEME_HOLO_LIGHT, null, endDateYear,
//                endDateMonth, endDateDay, "结束时间选择");
//
//        datePickerDialogEnd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel), (DialogInterface.OnClickListener) null);
//
//        datePickerDialogEnd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
//            @Override
//            @SuppressLint("NewApi")
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == DialogInterface.BUTTON_POSITIVE) {
//                    // Do Stuff
//                    endDateYear = datePickerDialogEnd.getDatePicker().getYear();
//                    endDateMonth = datePickerDialogEnd.getDatePicker().getMonth();
//                    endDateDay = datePickerDialogEnd.getDatePicker().getDayOfMonth();
//
//                }
//            }
//        });
//
////        if (tvIdcardStartDate.getText().toString().contains("-")) {
////            datePickerDialogEnd.getDatePicker().setMinDate(TimeUtil.datePlus(tvIdcardStartDate.getText().toString(), +1).getTime());
//////            datePickerDialogEnd.getDatePicker().setMinDate(TimeUtil.str2Date(tvIdcardStartDate.getText().toString()).getTime());
////        } else {
////            datePickerDialogEnd.getDatePicker().setMinDate((new Date()).getTime());
////        }
//
//
////        datePickerDialog.getDatePicker().setMinDate((new Date()).getTime());
//        datePickerDialogEnd.setCanceledOnTouchOutside(true);
//        datePickerDialogEnd.show();
//    }
//
//
////    private int startDateYear;
////    private int startDateMonth;
////    private int startDateDay;
//
//
////    String validPeriodInput = "";
////    Date dateStart;
////    Date dateEnd;
//
//
//    private boolean validate() {
//
////
////        if (StringUtilBle.isEmpty(tvIdcardStartDate.getText().toString())) {
////            DlgUtil.showToast(LockOpenRecordListActivity.this, getString(R.string.error_lock_share_start_date_input_none));
////            return false;
////        }
////        if (StringUtilBle.isEmpty(tvIdcardEndDate.getText().toString())) {
////            DlgUtil.showToast(LockOpenRecordListActivity.this, getString(R.string.error_lock_share_valid_period_input_none));
////            return false;
////        }
////
////        if (LockType.LOCK_AUTH_IDCARD.equalsIgnoreCase(actionType1)
////                || LockType.LOCK_AUTH_CARD_A.equalsIgnoreCase(actionType1)) {
////            if (StringUtilBle.isEmpty(edtCardAlias.getText().toString())) {
////                DlgUtil.showToast(LockOpenRecordListActivity.this, getString(R.string.error_lock_share_idcard_alias_input_none));
////                return false;
////            }
////        }
//
//
//        return true;
//    }
//
//    private boolean validateAuthAssignment() {
//
//
//        return true;
//    }
//
//
//    String assignmentType = "";


}
