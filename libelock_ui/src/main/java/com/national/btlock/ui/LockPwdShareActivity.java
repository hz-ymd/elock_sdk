package com.national.btlock.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.utils.DateTimeUtil;
import com.national.btlock.utils.DlgUtil;
import com.national.btlock.utils.TimeUtil;
import com.national.btlock.widget.datepick.CustomDatePicker;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.LongLockPwdEntity;
import com.national.core.nw.it.OnResultListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//20190218
public class LockPwdShareActivity extends BaseActivity implements AppConstants, OnClickListener {
    TextView tvRegister;
    String lockMac;
    String actionType;
    int authListNo = 0;
    TextView id_hint;
    int btnClick = 0;


    private CustomDatePicker customDatePicker1, customDatePicker2;


    private String getMaxDateText() {

        return DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
    }


    private String getDefaultEndDate() {

        Date now = DateTimeUtil.str2DateTime(tvStartDate.getText().toString());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        Date endDateInit = DateTimeUtil.addDay(now, 7);
        //一次性密码2小时
        if (chkPwdType.isChecked()) {
            endDateInit = DateTimeUtil.addHour(now, 2);
        }

        String endDate = TimeUtil.datetime2Str(endDateInit);

        if (DateTimeUtil.datetimeCompare(endDate, endTime) < 0) {
            endDate = endTime;
        }

        return endDate;
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        tvStartDate.setText(now);

        Date start = new Date();//DateTimeUtil.addYear(new Date(), -10);
        String startStr = sdf.format(start);
        String endStr;
        if (chkPwdType.isChecked()) {
            endStr = sdf.format(DateTimeUtil.addDay(new Date(), 7));
        } else {
            endStr = sdf.format(DateTimeUtil.addDay(new Date(), 365));
        }
        //String endStr = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
        if (!TextUtils.isEmpty(endTime)) {
            if (DateTimeUtil.datetimeCompare(endStr, endTime) < 0) {
                endStr = endTime;
            }
        }
        tvEndDate.setText(getDefaultEndDate());


        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                if (DateTimeUtil.datetimeCompare(time, tvEndDate.getText().toString()) >= 0) {
                    Date startAdd;
                    if (chkPwdType.isChecked()) {
                        startAdd = DateTimeUtil.addDay(DateTimeUtil.str2DateTime(time), 7);
                    } else {
                        startAdd = DateTimeUtil.addDay(DateTimeUtil.str2DateTime(time), 365);
                    }

                    Date end = DateTimeUtil.str2DateTime(tvEndDate.getText().toString());
                    if (end.getTime() > startAdd.getTime()) {
                        if (chkPwdType.isChecked()) {
                            DlgUtil.showToast(LockPwdShareActivity.this, "访客码授权不可超过7天");
                        } else {
                            DlgUtil.showToast(LockPwdShareActivity.this, "访客码授权不可超过1年");
                        }


                    } else {
                        tvStartDate.setText(time);
                        String endTime = tvEndDate.getText().toString().substring(0, time.lastIndexOf(":"))
                                + time.substring(time.lastIndexOf(":"));
                        tvEndDate.setText(endTime);
                    }


                }


            }
        }, startStr, endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.setMiuteEnable(true);
        customDatePicker1.showSpecificTime(true); // 不显示时和分
        customDatePicker1.setIsLoop(true); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间

                if (DateTimeUtil.datetimeCompare(tvStartDate.getText().toString(), time) >= 0) {
                    Date startAdd;
                    if (chkPwdType.isChecked()) {
                        startAdd = DateTimeUtil.addDay(DateTimeUtil.str2DateTime(tvStartDate.getText().toString()), 7);
                    } else {
                        startAdd = DateTimeUtil.addDay(DateTimeUtil.str2DateTime(tvStartDate.getText().toString()), 365);
                    }


                    Date end = DateTimeUtil.str2DateTime(time);
                    if (end.getTime() > startAdd.getTime()) {
                        if (chkPwdType.isChecked()) {
                            DlgUtil.showToast(LockPwdShareActivity.this, "访客码授权不可超过7天");
                        } else {
                            DlgUtil.showToast(LockPwdShareActivity.this, "访客码授权不可超过1年");
                        }
                    } else {
                        time = time.substring(0, time.lastIndexOf(":"))
                                + tvStartDate.getText().toString().substring(tvStartDate.getText().toString().lastIndexOf(":"));

                        tvEndDate.setText(time);
                    }
                }

            }
        }, startStr, endStr); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.setMiuteEnable(false);
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

    String endTime = "";
    Integer lockPwdListNo = 0;

    String lock_auth_need_real_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        setActionBarVisiblity(false);

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_acitivity_lock_pwd_share);

        setTitle("设备详情");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getStringExtra("lockMac") != null) {
            lockMac = getIntent().getStringExtra("lockMac");
        }

        if (getIntent().getStringExtra("authIdcardNeedRealName") != null) {
            lock_auth_need_real_name = getIntent().getStringExtra("authIdcardNeedRealName");
        }

        if (getIntent() != null) {
            authListNo = getIntent().getIntExtra("lock_auth_list_no", 0);
            lockPwdListNo = getIntent().getIntExtra("lock_pwd_list_no", 0);
//            idcardNo = getIntent().getStringExtra(AppConstants.IDCARD_NO);
//            idcardName = getIntent().getStringExtra(AppConstants.IDCARD_NAME);
            endTime = getIntent().getStringExtra("lock_auth_endtime");

            if (!TextUtils.isEmpty(endTime) && endTime.contains("长期")) {
                endTime = DateTimeUtil.longToDateStr2(2145888000000L - 1000L);
            }

        }


        initComponents();


        initDatePicker();


        findViewById(R.id.id_select_address).setOnClickListener(this);


//        initShareToWX();

        if (lock_auth_need_real_name.equals("1")) {
            chkPwdType.setEnabled(false);
            id_hint.setText(getResources().getString(R.string.txt_lock_pwd_valid_once_only_real_hint));
        } else {
            chkPwdType.setEnabled(true);
            id_hint.setText(getResources().getString(R.string.txt_lock_pwd_valid_once_only_hint));
        }


    }

//    WXShare wxShare;
//
//    private void initShareToWX() {
//        wxShare = new WXShare(this);
//        wxShare.setListener(new OnResponseListener() {
//            @Override
//            public void onSuccess() {
//                // 分享成功
//                MyLog.i(TAG, "分享成功");
////                LockPwdShareActivity.this.finish();
//            }
//
//            @Override
//            public void onCancel() {
//                // 分享取消
//                MyLog.i(TAG, "分享取消");
//            }
//
//            @Override
//            public void onFail(String message) {
//                // 分享失败
//                MyLog.i(TAG, "分享失败");
//                ToastUtil.showMessageL(message);
//            }
//        });
//
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        wxShare.register();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        wxShare.unregister();

        customDatePicker1.finish();
        customDatePicker1 = null;
        customDatePicker2.finish();
        customDatePicker2 = null;


    }


    TextView tvStartDate;
    TextView tvEndDate;
    String userId = "";
    String targetUserId;

    String uid;
    String dnCode;
    //    LinearLayout llCardAlias;
    EditText edtCardAlias;
    CheckBox chkPwdType;

    String authIdcardNeedRealName = "0";
    String lockVer;

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            initDatePicker();
            tvEndDate.setText(getDefaultEndDate());

        }
    };

    String userName = "";

    boolean invented = false;

    private void initComponents() {

        edtCardAlias = findViewById(R.id.id_idcard_alias_input);

        id_hint = findViewById(R.id.id_hint);
        chkPwdType = findViewById(R.id.chk_same_end_time);
        chkPwdType.setOnCheckedChangeListener(mOnCheckedChangeListener);

        tvRegister = findViewById(R.id.id_btn_next);
        tvRegister.setOnClickListener(this);

        findViewById(R.id.id_btn_wx_send).setOnClickListener(this);
        tvStartDate = findViewById(R.id.id_idcard_start_date_input);
        tvEndDate = findViewById(R.id.id_idcard_date_valid_period_input);


        ImageView imgvMore = findViewById(R.id.id_idcard_date_valid_period_input_more);
        imgvMore.setOnClickListener(this);


        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        Intent intent = getIntent();

        if (intent != null) {

            if (intent.hasExtra("user_name")
                    || intent.hasExtra("user_id")
            ) {
                userName = intent.getStringExtra("user_name");
                targetUserId = intent.getStringExtra("user_id");
            } else if (intent.hasExtra("uid")) {
//                isIdcard =true;

                uid = intent.getStringExtra("uid");
                dnCode = intent.getStringExtra("dnCode");
            }


            if (getIntent().getStringExtra("lock_mac") != null) {
                lockMac = getIntent().getStringExtra("lock_mac");
            }

            if (getIntent().getStringExtra("action_type") != null) {
                actionType = getIntent().getStringExtra("action_type");
            }
            if (getIntent().getStringExtra("lock_auth_need_real_name") != null) {
                authIdcardNeedRealName = getIntent().getStringExtra("lock_auth_need_real_name");
            }
            if (getIntent().getStringExtra("lock_ver") != null) {
                lockVer = getIntent().getStringExtra("lock_ver");
            }


            if (TextUtils.isEmpty(lockVer)
                    || "1.04".equalsIgnoreCase(lockVer)
                    || "1.03".equalsIgnoreCase(lockVer)
                    || "1.02".equalsIgnoreCase(lockVer)
                    || "1.01".equalsIgnoreCase(lockVer)) {
                authIdcardNeedRealName = "0";
            }
        }

        setTitle(getString(R.string.txt_tmp_pwd_share));
        if (getIntent().getExtras().getString("lockAttribute") != null) {
            String lockAttribute = getIntent().getExtras().getString("lockAttribute");
            if (!TextUtils.isEmpty(lockAttribute) && lockAttribute.length() == 8 && lockAttribute.substring(7, 8).equals("0")) {
                setTitle(getString(R.string.txt_tmp_invented_pwd_share));
                invented = true;
            }
        }


        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        edtAccount = findViewById(R.id.id_lock_share_accout_input);
        edtAccount.addTextChangedListener(mTextWatcher);
        chkPwdType.setChecked(true);

    }

    ClipboardManager mClipboardManager;

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            // 粘贴板有数据，并且是文本
            if (mClipboardManager.hasPrimaryClip()
                    && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                CharSequence text = item.getText();
                if (text == null) {
                    return;
                }

                String tt = text.toString();

                if (TextUtils.isEmpty(tt)) {
                    return;
                }


                if (text.toString().length() > 11) {


                    String phoneNo = tt.substring(tt.length() - 11);


                    edtAccount.removeTextChangedListener(mTextWatcher);
                    edtAccount.setText(phoneNo);
                    mClipboardManager.setText("");

                    edtAccount.addTextChangedListener(mTextWatcher);
                }

            }

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    boolean isAccountVerified() {
        return !"未注册用户".equalsIgnoreCase(userName)
                && !"未实名用户".equalsIgnoreCase(userName);
    }

//    @Override
//    public boolean handleMessage(Message msg) {
//        switch (msg.what) {
//            case EVENT_NEXT:
//                doLockShare();
//                break;
//            case EVENT_MSG:
//                LockPwdShareActivity.this.finish();
//                break;
//            case LoginModel.SHARE_LONG_LOCK_PWD: {
//                int success = msg.arg1;
//                if (success == SUCCESS) {
//                    LongLockPwdEntity entity = (LongLockPwdEntity) msg.obj;
//                    MyApp.getInstance().setDoNothing(true);
//                    if (btnClick == 1) {
//                        sendSysMsg(edtAccount.getText().toString(), entity.getData().getPwdShareStr());
////                        this.finish();
//                    } else if (btnClick == 2) {
////                        MyApp.getInstance().doNothing = true;
//
//                        wxShare.shareTxt(getString(R.string.txt_tmp_pwd_share), entity.getData().getPwdShareStr());
////                        LockPwdShareActivity.this.finish();
//                    }
//
//                } else {
//                    String message = (String) msg.obj;
//                    if (!TextUtils.isEmpty(message)) {
//                        DlgUtil.showToast(LockPwdShareActivity.this, message);
//                    }
//
//
//                }
//            }
//            break;
//
//
//            default:
//                break;
//        }
//        return false;
//    }


    private void doLockShare() {


        String pwdType;
        if (chkPwdType.isChecked()) {
            pwdType = "0";
        } else {
            pwdType = "1";

        }
        SDKCoreHelper.shareVistorPwd(lockMac, edtAccount.getText().toString(), tvStartDate.getText().toString()+":00",
                tvEndDate.getText().toString()+":00", edtCardAlias.getText().toString(), pwdType, new OnResultListener() {
                    @Override
                    public void onSuccess(String s) {

                        LongLockPwdEntity entity = new Gson().fromJson(s, LongLockPwdEntity.class);
                        if (entity != null) {


                            if (btnClick == 1) {
                                sendSysMsg(edtAccount.getText().toString(), entity.getData().getPwdShareStr());
//                        this.finish();
                            } else if (btnClick == 2) {
//                        MyApp.getInstance().doNothing = true;

                                //wxShare.shareTxt(getString(R.string.txt_tmp_pwd_share), entity.getData().getPwdShareStr());
//                        LockPwdShareActivity.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onError(String s, String s1) {
                        DlgUtil.showToast(LockPwdShareActivity.this, s1);

                    }
                });
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

    private static final int PICK_CONTACT = 5001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void doContacts() {
//        MyApp.getInstance().doNothing = true;
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }


    private EditText edtAccount;

    private void showMethodSelectDlg(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LockPwdShareActivity.this, AlertDialog.THEME_HOLO_LIGHT);

        final String[] cities = data.split(",");
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                edtAccount.setText(cities[which]);
            }
        });

        if (!isFinishing()) {
            builder.show();
        }


    }


    @Override
    public void onClick(View v) {
//        Map<String, String> requestParams = new HashMap<String, String>();

        int id = v.getId();
        if (id == R.id.id_select_address) {//                doContacts();
            boolean contactsPermission =
//                pkgManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
                    ActivityCompat.checkSelfPermission(LockPwdShareActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT >= 23
                    || !contactsPermission
            ) {

                requestPermission();
            } else {
                doContacts();
            }
        } else if (id == R.id.id_btn_wx_send) {
            if (!validate()) {
                return;
            }

            btnClick = 2;
            doLockShare();
        } else if (id == R.id.id_btn_next) {
            if (!validate()) {
                return;
            }
            btnClick = 1;
            doLockShare();
        } else if (id == R.id.id_idcard_start_date_input) {
            customDatePicker1.show(tvStartDate.getText().toString());
//                showStartDatePickerDlg();
        } else if (id == R.id.id_idcard_date_valid_period_input) {
            customDatePicker2.show(tvEndDate.getText().toString());
//                showEndDatePickerDlg();
        } else if (id == R.id.id_idcard_date_valid_period_input_more) {//                if (chkPwdType.isChecked()) {
            showOneTimeDlgInput();
//                } else {
//
//                    showValidPeriodInput();
//                }
        }

    }


    Date dateStart;
    Date dateEnd;

    private void showOneTimeDlgInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LockPwdShareActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        //    指定下拉列表的显示数据
        String[] cities;
        if (chkPwdType.isChecked()) {
            cities = new String[]{"1天", "2天", "3天", "4天", "5天", "6天", "7天"};
        } else {
            cities = new String[]{"1天", "2天", "3天", "4天", "5天", "6天", "7天", "1月", "6月", "1年"};
        }
        //    设置一个下拉的列表选择项
        builder
                .setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                tvIdcardEndDate.setText(cities[which]);

                        String startDateStr = tvStartDate.getText().toString();
//                        dateStart = DateTimeUtil.str2Date(startDateStr);

                        if (!startDateStr.contains(":")) {
                            startDateStr = startDateStr + " 12:00";
                        }

                        dateStart = DateTimeUtil.str2DateTime(startDateStr);
                        if (dateStart == null) {
                            return;
                        }

                        switch (which) {
                            case 0:
//                                validPeriodInput = "1日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 1);
                                break;
                            case 1:
//                                validPeriodInput = "2日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 2);
                                break;
                            case 2:
//                                validPeriodInput = "3日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 3);
                                break;
                            case 3:
//                                validPeriodInput = "1月";
                                dateEnd = DateTimeUtil.addDay(dateStart, 4);
                                break;
                            case 4:
//                                validPeriodInput = "2月";
                                dateEnd = DateTimeUtil.addDay(dateStart, 5);
                                break;
                            case 5:
//                                validPeriodInput = "3月";
                                dateEnd = DateTimeUtil.addDay(dateStart, 6);
                                break;
                            case 6:
//                                validPeriodInput = "1年";
                                dateEnd = DateTimeUtil.addDay(dateStart, 7);
                                break;
                            case 7:
                                dateEnd = DateTimeUtil.addMonth(dateStart, 1);
                                break;
                            case 8:
                                dateEnd = DateTimeUtil.addMonth(dateStart, 6);
                                break;
                            case 9:
                                dateEnd = DateTimeUtil.addYear(dateStart, 1);
                                break;
                            default:
                                break;

                        }

//                        tvEndDate.setText(TimeUtil.datetime2Str(dateEnd) );//+ " 12:00"
                        if (validDate(dateStart, dateEnd)) {

                            if (DateTimeUtil.datetimeCompare(TimeUtil.datetime2Str(dateEnd), endTime) >= 0) {
                                tvEndDate.setText(TimeUtil.datetime2Str(dateEnd));
                            } else {
                                tvEndDate.setText(endTime);
                            }

//                            showValidTimeInput(TimeUtil.datetime2Str(dateEnd));

                        } else {
//                            DlgUtil.showToast(LockPwdShareActivity.this, "选择的延长日期不能超过最大值（2038-01-01 00:00）");
                        }


                    }
                });


        builder.show();


    }


    private void showValidPeriodInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LockPwdShareActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        //    指定下拉列表的显示数据
        final String[] cities = {"1天", "2天", "3天", "1月", "2月", "3月", "1年"};
        //    设置一个下拉的列表选择项
        builder
                .setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                tvIdcardEndDate.setText(cities[which]);

                        String startDateStr = tvStartDate.getText().toString();
//                        dateStart = DateTimeUtil.str2Date(startDateStr);

                        if (!startDateStr.contains(":")) {
                            startDateStr = startDateStr + " 12:00";
                        }

                        dateStart = DateTimeUtil.str2DateTime(startDateStr);
                        if (dateStart == null) {
                            return;
                        }

                        switch (which) {
                            case 0:
//                                validPeriodInput = "1日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 1);
                                break;
                            case 1:
//                                validPeriodInput = "2日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 2);
                                break;
                            case 2:
//                                validPeriodInput = "3日";
                                dateEnd = DateTimeUtil.addDay(dateStart, 3);
                                break;
                            case 3:
//                                validPeriodInput = "1月";
                                dateEnd = DateTimeUtil.addMonth(dateStart, 1);
                                break;
                            case 4:
//                                validPeriodInput = "2月";
                                dateEnd = DateTimeUtil.addMonth(dateStart, 2);
                                break;
                            case 5:
//                                validPeriodInput = "3月";
                                dateEnd = DateTimeUtil.addMonth(dateStart, 3);
                                break;
                            case 6:
//                                validPeriodInput = "1年";
                                dateEnd = DateTimeUtil.addYear(dateStart, 1);
                                break;

                            default:
                                break;

                        }

//                        tvEndDate.setText(TimeUtil.datetime2Str(dateEnd) );//+ " 12:00"
                        if (validDate(dateStart, dateEnd)) {
//                            tvEndDate.setText(TimeUtil.datetime2Str(dateEnd));

                            String now = TimeUtil.datetime2Str(dateEnd);
                            if (DateTimeUtil.datetimeCompare(now, endTime) >= 0) {
                                tvEndDate.setText(TimeUtil.datetime2Str(dateEnd));
                            } else {
                                tvEndDate.setText(endTime);
                            }


                        } else {
//                            DlgUtil.showToast(LockPwdShareActivity.this, "选择的延长日期不能超过最大值（2038-01-01 00:00）");
                        }


                    }
                });


        builder.show();


    }


    boolean validDate(Date start, Date end) {

        if (end.getTime() > Long.valueOf("2145888000000")) {
            return false;
        }
        return start.getTime() < end.getTime();
    }

    private boolean validate() {
        Date startAdd7 = DateTimeUtil.addDay(DateTimeUtil.str2DateTime(tvStartDate.getText().toString()), 7);
        Date end = DateTimeUtil.str2DateTime(tvEndDate.getText().toString());
        if (end.getTime() > startAdd7.getTime()) {
            DlgUtil.showToast(LockPwdShareActivity.this, "访客码授权不可超过7天");
            return false;
        }


        if (TextUtils.isEmpty(edtAccount.getText().toString())) {
            DlgUtil.showToast(LockPwdShareActivity.this, getString(R.string.lock_share_accout_input_none));
            return false;
        }

        if (edtAccount.getText().toString().length() != 11 || !edtAccount.getText().toString().startsWith("1")) {
            DlgUtil.showToast(LockPwdShareActivity.this, getString(R.string.error_tel_no_input_error));
            return false;
        }


        if (TextUtils.isEmpty(edtCardAlias.getText().toString())) {
            DlgUtil.showToast(LockPwdShareActivity.this, getString(R.string.error_pwd_name_input_none));
            return false;
        }

        if (TextUtils.isEmpty(tvStartDate.getText().toString())) {
            DlgUtil.showToast(LockPwdShareActivity.this, getString(R.string.error_lock_share_start_date_input_none));
            return false;
        }
        if (TextUtils.isEmpty(tvEndDate.getText().toString())) {
            DlgUtil.showToast(LockPwdShareActivity.this, getString(R.string.error_lock_share_valid_period_input_none));
            return false;
        }


        return true;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(LockPwdShareActivity.this
                , permsion
                , REQUEST_PERMISSION);
        isPermissionsNotGranted = false;

    }


    String[] permsion = new String[]{
            Manifest.permission.READ_CONTACTS

    };

    private static final int REQUEST_PERMISSION = 1000;

    volatile boolean isGranted = false;

    volatile boolean isPermissionsNotGranted = false;

    private void initAfterGranted() {
        doContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {

            if (isGranted) {
                return;
            }

            if (grantResults.length == permsion.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {

                        //2018/11/13
//                        intiPermission();

                        isPermissionsNotGranted = true;

                        break;
                    }
                }

                if (isPermissionsNotGranted) {
                    showDeniedDialog();
                } else {
                    initAfterGranted();
                }

            } else {


//                intiPermission();
                Toast.makeText(getApplicationContext(), R.string.app_permission_required, Toast.LENGTH_LONG)
                        .show();


                finish();


            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    private synchronized void showDeniedDialog() {
//        MyApp.getInstance().doNothing = true;
        AlertDialog dlg = new AlertDialog.Builder(LockPwdShareActivity.this)
                .setMessage(R.string.app_permission_required)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        LockPwdShareActivity.this.finish();
                    }
                })
                .setPositiveButton(R.string.settings_btn_txt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSetting();
                    }
                }).create();

        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);

        if (!isFinishing()) {
            dlg.show();
        }

    }

    private static final int REQUEST_SETTING = 0x39;

    private void startSetting() {
//        MyApp.getInstance().doNothing = true;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_SETTING);
    }
}
