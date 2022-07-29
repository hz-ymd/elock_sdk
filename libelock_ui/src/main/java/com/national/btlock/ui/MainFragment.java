package com.national.btlock.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.national.btlock.adapter.FunctionGridAdapter;
import com.national.btlock.model.AppItem;
import com.national.btlock.ui.bannerview.BannerView;
import com.national.btlock.ui.bannerview.ViewFlowAdapter;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.widget.MySlidingDrawer;
import com.national.btlock.widget.SimpleProgressDialog;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.DeviceDetailEntity;
import com.national.core.nw.entity.LockListEntity;
import com.national.core.nw.it.OnProgressUpdateListener;
import com.national.core.nw.it.OnResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements View.OnClickListener, AppConstants {

    private static final String TAG = "MainFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    BannerView bannerView;
    MySlidingDrawer sliding_drawer;
    View layout_no_lock;
    //    ProgressBar loading;
    ImageView image_list, image_search;


    LinearLayout content;
    GridView grid_func;
    List<AppItem> mList;
    FunctionGridAdapter adapter;
    TextView auth_app, auth_idcard, auth_card, auth_pwd;
    TextView text_owner, text_manager, text_manager_time;
    LinearLayout layout_auth;
    RelativeLayout layout_owner_manager;
    TextView assignment_auth, assignment_take_back, assignment_extend, assignment_give_back;


    String endTime = "";

    SimpleProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

//        loading = view.findViewById(R.id.loading);
        image_list = view.findViewById(R.id.image_list);
        image_search = view.findViewById(R.id.image_search);

        bannerView = view.findViewById(R.id.banner_locks);
        sliding_drawer = view.findViewById(R.id.sliding_drawer);
        sliding_drawer.setHandleId(R.id.view_handler);
        sliding_drawer.setTouchableIds(new int[]{R.id.grid_func});
        layout_no_lock = view.findViewById(R.id.layout_no_lock);

        image_list.setOnClickListener(this);
        image_search.setOnClickListener(this);
        //-------

        grid_func = view.findViewById(R.id.grid_func);
        layout_auth = view.findViewById(R.id.layout_auth);
        auth_app = view.findViewById(R.id.auth_app);
        auth_idcard = view.findViewById(R.id.auth_idcard);
        auth_card = view.findViewById(R.id.auth_card);
        auth_pwd = view.findViewById(R.id.auth_pwd);
        text_owner = view.findViewById(R.id.text_owner);
        text_manager = view.findViewById(R.id.text_manager);
        text_manager_time = view.findViewById(R.id.text_manager_time);

        assignment_auth = view.findViewById(R.id.assignment_auth);
        assignment_give_back = view.findViewById(R.id.assignment_give_back);
        assignment_take_back = view.findViewById(R.id.assignment_take_back);
        assignment_extend = view.findViewById(R.id.assignment_extend);


        content = view.findViewById(R.id.content);
        layout_owner_manager = view.findViewById(R.id.layout_owner_manager);
        layout_owner_manager.setOnClickListener(this);

        view.findViewById(R.id.btn_refresh).setOnClickListener(this);
        view.findViewById(R.id.image_refresh).setOnClickListener(this);

        view.findViewById(R.id.assignment_extend).setOnClickListener(this);
        view.findViewById(R.id.assignment_auth).setOnClickListener(this);
        view.findViewById(R.id.assignment_give_back).setOnClickListener(this);
        view.findViewById(R.id.assignment_take_back).setOnClickListener(this);
        mList = new ArrayList<>();
        adapter = new FunctionGridAdapter(getActivity(), mList);
        grid_func.setAdapter(adapter);// 调用ImageAdapter

        auth_app.setOnClickListener(this);
        auth_idcard.setOnClickListener(this);
        auth_card.setOnClickListener(this);
        auth_pwd.setOnClickListener(this);
        initAppItem();

        sliding_drawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                addAppItem();
                getLockDetail();
            }
        });

        sliding_drawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                initAppItem();
                content.setVisibility(View.GONE);
            }
        });


        //获取缓存锁列表
        getOfflineLockList();

        return view;


    }

    DeviceDetailEntity deviceDetailEntity;


    private void showProgressDialog() {
        pd = new SimpleProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.show();
    }


    private void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    public void getLockDetail() {
        layout_auth.setVisibility(View.GONE);
        layout_owner_manager.setVisibility(View.GONE);

        lock = lockList.get(bannerView.getPosition());
        showProgressDialog();
        SDKCoreHelper.getLockDetails(lock.getMac(), new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Log.d(TAG, "json:" + jsonStr);
                deviceDetailEntity = new Gson().fromJson(jsonStr, DeviceDetailEntity.class);
                if (deviceDetailEntity != null) {

                    Log.d(TAG, lock.getLockName() + ":" + lock.getOwnerType());
                    String ownerType = lock.getOwnerType();
                    if (ownerType.equals(LockOwnerType.O_M) || ownerType.equals(LockOwnerType.M)) {
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


                    assignment_auth.setVisibility(View.GONE);
                    assignment_take_back.setVisibility(View.GONE);
                    assignment_extend.setVisibility(View.GONE);
                    assignment_give_back.setVisibility(View.GONE);

                    if (ownerType.equals(LockOwnerType.O) || ownerType.equals(LockOwnerType.O_U)) {
                        assignment_auth.setVisibility(View.VISIBLE);
                        assignment_take_back.setVisibility(View.VISIBLE);
                        assignment_extend.setVisibility(View.VISIBLE);
                    }

                    if (ownerType.equals(LockOwnerType.O_M)) {
                        assignment_auth.setVisibility(View.VISIBLE);
                    }

                    if (ownerType.equals(LockOwnerType.M)) {
                        assignment_give_back.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d(TAG, errorCode + "," + errorMsg);
                dismissProgressDialog();
                Toast.makeText(getActivity(), "获取详情失败" + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    ArrayList<LockListEntity.Lock> lockList = new ArrayList<>();
    boolean getDetail = false;
    int selection = 0;

    private void getLockList() {
        selection = 0;
        getDetail = false;
        showProgressDialog();
        SDKCoreHelper.getLockList(new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                Log.d(TAG, jsonStr);
                LockListEntity lockListEntity = new Gson().fromJson(jsonStr, LockListEntity.class);
                if (lockListEntity.getData() != null) {
                    lockList = lockListEntity.getData();
                    if (lockList != null && lockList.size() != 0) {
                        if (lock != null) {
                            for (int i = 0; i < lockList.size(); i++) {
                                if (lock.getLockId().equals(lockList.get(i).getLockId())) {
                                    selection = i;
                                    break;
                                }
                            }
                        }
                        sliding_drawer.setVisibility(View.VISIBLE);
                        bannerView.setVisibility(View.VISIBLE);
                        layout_no_lock.setVisibility(View.GONE);
                        bannerView.showBanner(lockList, false, selection, new ViewFlowAdapter.OnImageClickLinstener() {
                            @Override
                            public void onClick(LockListEntity.Lock entiy) {
//                                SDKCoreHelper.openLock(entiy.getMac(), new OnProgressUpdateListener() {
//                                    @Override
//                                    public void onProgressUpdate(String message) {
//                                        Log.d(TAG, "onProgressUpdate:" + message);
//                                    }
//
//                                    @Override
//                                    public void onSuccess(String jsonStr) {
//                                        Log.d(TAG, "onSuccess:" + jsonStr);
//                                    }
//
//                                    @Override
//                                    public void onError(String errorCode, String errorMsg) {
//                                        Log.d(TAG, "onError:" + errorCode + "," + errorMsg);
//                                    }
//                                });

                            }
                        }, new BannerView.OnChangeListener() {
                            @Override
                            public void onChange(int position) {

                                if (getDetail) {
                                    if (sliding_drawer.isOpened()) {
                                        getLockDetail();
                                    }
                                } else {
                                    getDetail = true;
                                }

                            }
                        });
                    } else {
                        sliding_drawer.setVisibility(View.GONE);
                        layout_no_lock.setVisibility(View.VISIBLE);
                        bannerView.setVisibility(View.GONE);
                    }

                }


            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
//                sliding_drawer.setVisibility(View.GONE);
//                bannerView.setVisibility(View.GONE);
//                layout_no_lock.setVisibility(View.VISIBLE);
                Log.d(TAG, errorCode + "," + errorMsg);

                Toast.makeText(getActivity(), "设备列表获取失败：" + errorMsg, Toast.LENGTH_LONG).show();


            }
        });
    }


    private void getOfflineLockList() {
        getDetail = false;
        SDKCoreHelper.getOffLineLockList(new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                Log.d(TAG, jsonStr);
                LockListEntity lockListEntity = new Gson().fromJson(jsonStr, LockListEntity.class);
                if (lockListEntity.getData() != null) {
                    lockList = lockListEntity.getData();
                    if (lockList != null && lockList.size() != 0) {
                        sliding_drawer.setVisibility(View.VISIBLE);
                        bannerView.setVisibility(View.VISIBLE);
                        layout_no_lock.setVisibility(View.GONE);
                        bannerView.showBanner(lockList, false, selection, new ViewFlowAdapter.OnImageClickLinstener() {
                            @Override
                            public void onClick(LockListEntity.Lock entiy) {
                                SDKCoreHelper.openLock(entiy.getMac(), new OnProgressUpdateListener() {
                                    @Override
                                    public void onProgressUpdate(String message) {
                                        Log.d(TAG, "onProgressUpdate:" + message);
                                    }

                                    @Override
                                    public void onSuccess(String jsonStr) {
                                        Log.d(TAG, "onSuccess:" + jsonStr);
                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMsg) {
                                        Log.d(TAG, "onError:" + errorCode + "," + errorMsg);
                                    }
                                });
                            }
                        }, new BannerView.OnChangeListener() {
                            @Override
                            public void onChange(int position) {
                                if (getDetail) {
                                    if (sliding_drawer.isOpened()) {
                                        getLockDetail();
                                    }
                                } else {
                                    getDetail = true;
                                }
                            }
                        });
                    } else {
                        sliding_drawer.setVisibility(View.GONE);
                        layout_no_lock.setVisibility(View.VISIBLE);
                        bannerView.setVisibility(View.GONE);
                    }
                }
                getSysConfig();

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                sliding_drawer.setVisibility(View.GONE);
                bannerView.setVisibility(View.GONE);
                layout_no_lock.setVisibility(View.VISIBLE);
                getSysConfig();
            }
        });
    }


    private void getSysConfig() {
        showProgressDialog();
        SDKCoreHelper.getSysConfig(new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                getLockList();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                dismissProgressDialog();

            }
        });

    }


    public void initAppItem() {


        mList.clear();
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
//        item.setResId(R.drawable.icon_device_manager);
//        mList.add(item);
//
//
//        item = new AppItem();
//        item.setAppName(getString(R.string.device_data));
//        item.setResId(R.drawable.icon_data);
//        mList.add(item);
        adapter.notifyDataSetChanged();


        grid_func.setOnItemClickListener((adapterView, view, i, l) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
            } else {
                Log.w(TAG, "点击过快");
                return;
            }
            switch (mList.get(i).getAppName()) {
                case "用户授权":
                    goNext(LockType.LOCK_SHARE);
                    break;
                case "钥匙卡授权":
                    goNext(LockType.LOCK_AUTH_CARD_A);
                    break;
                case "身份证授权":
                    goNext(LockType.LOCK_AUTH_IDCARD);
                    break;
                case "访客码授权":
                    goNext(LockType.LOCK_LONG_PWD_SET);
                    break;
                case "数据查询":
                    goNext(LockType.GET_RECORD);
                    break;
                default:
                    Toast.makeText(getActivity(), "开发中，敬请期待", Toast.LENGTH_SHORT).show();
                    break;
//                case "钥匙卡授权":
//                    break;
//                case "访客码授权":
//                    break;
//                case "更多功能":
//                    break;
            }

        });
    }


    public void addAppItem() {

        mList.clear();
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

    }

    CheckBox chkBoxDeleteAll;
    int actionType;
    LockListEntity.Lock lock;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        if (lockList != null && lockList.size() != 0) {
            lock = lockList.get(bannerView.getPosition());
            setEndTime(lock);
        }
        int id = view.getId();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
        } else {
            Log.w(TAG, "点击过快");
            return;
        }

        if (id == R.id.btn_refresh || id == R.id.image_refresh) {
            if (sliding_drawer.isOpened()) {
                sliding_drawer.close();
            }
            getLockList();
        } else if (id == R.id.image_list) {
            Intent intent = new Intent(getActivity(), LockListActivity.class);
            startActivity(intent);
        } else if (id == R.id.image_search) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        } else if (lock != null) {
            if (id == R.id.assignment_take_back) {
                //removeFlg 0:不删除 1:删除
                actionType = 2;
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View layout = inflater.inflate(R.layout.layout_dialog_chkbox, null);
                chkBoxDeleteAll = (CheckBox) layout.findViewById(R.id.chk_manager_give_back);//注意这一句
                chkBoxDeleteAll.setChecked(true);
                show2ndConfirmDlg(lock, layout, getString(R.string.auth_get_back_txt));

            } else if (id == R.id.assignment_auth) {
                goNext(LockType.LOCK_DELETE);
            } else if (id == R.id.assignment_give_back) {
                actionType = 1;
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View layout = inflater.inflate(R.layout.layout_dialog_chkbox, null);
                chkBoxDeleteAll = (CheckBox) layout.findViewById(R.id.chk_manager_give_back);//注意这一句
                chkBoxDeleteAll.setChecked(true);
                show2ndConfirmDlg(lock, layout, getString(R.string.auth_give_back_management_txt));

            } else if (id == R.id.assignment_extend) {
                //管理权授权调整
                Intent intent = new Intent(getActivity(), LockShareExtendActivity.class);
                intent.putExtra("extend_type", ExtendType.Managerment);
                intent.putExtra("user_id", deviceDetailEntity.getData().getManagerName());
                intent.putExtra("lockMac", lock.getMac());
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
                goAuthList(lock.getMac(), LockType.LOCK_SHARE);
            } else if (id == R.id.auth_idcard) {
                goAuthList(lock.getMac(), LockType.LOCK_AUTH_IDCARD);
            } else if (id == R.id.auth_card) {
                goAuthList(lock.getMac(), LockType.LOCK_AUTH_CARD_A);
            } else if (id == R.id.auth_pwd) {

                Intent intent = new Intent(getActivity(), LockPwdLongShareListActivity.class);
                intent.putExtra("lock_mac", lock.getMac());
                startActivity(intent);


            }
        }
    }

    public void setEndTime(LockListEntity.Lock lock) {
        endTime = "长期";
        String str = lock.getValidPeriodStr();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String startTime = sdf.format(new Date());

        String[] time = str.split("至");
        if (time.length == 2) {
            endTime = time[1];
            startTime = time[0];
        }
    }


    public void goAuthList(String lockMac, String actionType) {
        Intent intent = new Intent(getActivity(), AuthListActivity.class);
        intent.putExtra("lockMac", lockMac);
        intent.putExtra("lock_auth_endtime", endTime);
        intent.putExtra("action_type", actionType);
        startActivityForResult(intent, REQUEST_LOCK_AUTH);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private static final int REQUEST_LOCK_DELETE = 11111;
    private static final int REQUEST_LOCK_AUTH = 22222;
    private static final int REQUEST_LOCK_SHARE = 33333;
    private static final int REQ_EXTEND = 44444;
    private static final int REQUEST_LONG_PWD_SET = 55555;


    public void goNext(String actionType) {
        lock = lockList.get(bannerView.getPosition());
        if (actionType.equals(LockType.LOCK_SHARE) || actionType.equals(LockType.LOCK_AUTH_CARD_A)) {
            if (lock.getOwnerType().equals(LockOwnerType.M) || lock.getOwnerType().equals(LockOwnerType.O_M)) {
            } else {
                Toast.makeText(getActivity(), "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (actionType.equals(LockType.LOCK_AUTH_IDCARD)) {
            if (lock.getOwnerType().equals(LockOwnerType.O)) {
                Toast.makeText(getActivity(), "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }

            if (lock.getOwnerType().equals(LockOwnerType.O_U) && lock.getOwnerType().equals(LockOwnerType.U)) {
                if (lock.getAuthIdcardNeedRealName().equals("0")) {
                    Toast.makeText(getActivity(), "您没有权限", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        if (actionType.equals(LockType.LOCK_LONG_PWD_SET)) {
            if (lock.getOwnerType().equals(LockOwnerType.O)) {
                Toast.makeText(getActivity(), "您没有权限", Toast.LENGTH_LONG).show();
                return;
            }
        }


        String str = lock.getValidPeriodStr();
        String endTime = "长期";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String startTime = sdf.format(new Date());

        String[] time = str.split("至");
        if (time.length == 2) {
            endTime = time[1];
            startTime = time[0];
        }
        Intent intent;
        if (actionType.equals(LockType.LOCK_LONG_PWD_SET)) {
            intent = new Intent(getActivity(), LockPwdShareActivity.class);
        } else if (actionType.equals(LockType.GET_RECORD)) {
            intent = new Intent(getActivity(), OperateRecordActivity.class);
        } else {
            intent = new Intent(getActivity(), LockShareActivity.class);
        }

        intent.putExtra("action_type", actionType);
        intent.putExtra("lockMac", lock.getMac());
        intent.putExtra("lock_auth_endtime", endTime);
        intent.putExtra("ownerType", lock.getOwnerType());
        intent.putExtra("authIdcardNeedRealName", lock.getAuthIdcardNeedRealName());
        if (actionType.equals(LockType.LOCK_SHARE)) {
            startActivityForResult(intent, REQUEST_LOCK_SHARE);
        }
        if (actionType.equals(LockType.LOCK_DELETE)) {
            startActivityForResult(intent, REQUEST_LOCK_DELETE);
        }
        if (actionType.equals(LockType.LOCK_AUTH_CARD_A) || actionType.equals(LockType.LOCK_AUTH_IDCARD)) {
            startActivityForResult(intent, REQUEST_LOCK_SHARE);
        }

        if (actionType.equals(LockType.LOCK_LONG_PWD_SET)) {
            startActivityForResult(intent, REQUEST_LONG_PWD_SET);
        }

        if (actionType.equals(LockType.GET_RECORD)) {
            startActivity(intent);
        }


    }


    private void show2ndConfirmDlg(LockListEntity.Lock lock, View layout, final String pwdTitle) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        if (layout != null) {
            dlg.setView(layout);
        }
        dlg.setTitle(pwdTitle);
        dlg.setPositiveButton(pwdTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                giveBackManagement(lock, pwdTitle);
                dialog.dismiss();

            }
        });
        dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dlg.setCancelable(true);

        if (!getActivity().isFinishing()) {
            dlg.create().show();
        }

    }

    private void giveBackManagement(LockListEntity.Lock lock, String msg) {
        showProgressDialog();
        SDKCoreHelper.giveBackManagement(lock.getMac(), chkBoxDeleteAll.isChecked() ? "1" : "0", new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                dismissProgressDialog();
                sliding_drawer.close();
                Toast.makeText(getActivity(), msg + "成功", Toast.LENGTH_LONG).show();
                getLockList();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), msg + "失败：" + errorMsg, Toast.LENGTH_LONG).show();

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOCK_DELETE && resultCode == getActivity().RESULT_OK) {
            if (sliding_drawer.isOpened()) {
                sliding_drawer.close();
            }
            getLockList();
        }

        if (requestCode == REQUEST_LOCK_SHARE) {
            if (sliding_drawer.isOpened()) {
                getLockDetail();
            }
        }

        if (requestCode == REQ_EXTEND) {
            if (sliding_drawer.isOpened()) {
                getLockDetail();
            }
        }

        if (requestCode == REQUEST_LOCK_AUTH) {
            if (sliding_drawer.isOpened()) {
                getLockDetail();
            }
        }

        if (requestCode == REQUEST_LONG_PWD_SET) {
            if (sliding_drawer.isOpened()) {
                getLockDetail();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "isVisibleToUser");
        }
    }


}