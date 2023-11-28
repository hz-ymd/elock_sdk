package com.national.btlock.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
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
import com.national.btlock.utils.StringUtilBle;
import com.national.btlock.widget.MySlidingDrawer;
import com.national.btlock.widget.SimpleProgressDialog;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.entity.DeviceDetailEntity;
import com.national.core.nw.entity.LockListEntity;
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
    private static final String ARG_APPID = "appId";
    private static final String ARG_APPSECRET = "appSecret";
    private static final String ARG_LICENSEID = "licenseId";
    private static final String ARG_LICENSEFILENAME = "licenseFileName";
    private static final String ARG_USERNAME = "userName";

    // TODO: Rename and change types of parameters
    private String appId;
    private String appSececrt;
    private String licenseId;
    private String licenseFileName;
    private String userName;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String appId,
                                           String appSecret,
                                           String licenseid,
                                           String licenseFileName,
                                           String userName) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APPID, appId);
        args.putString(ARG_APPSECRET, appSecret);
        args.putString(ARG_LICENSEID, licenseid);
        args.putString(ARG_LICENSEFILENAME, licenseFileName);
        args.putString(ARG_USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    boolean isLogin = true;
    private static final int REQUEST_OPEN_BT_CODE = 2000;

    public void enableBlueTooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appId = getArguments().getString(ARG_APPID);
            appSececrt = getArguments().getString(ARG_APPSECRET);
            licenseId = getArguments().getString(ARG_LICENSEID);
            licenseFileName = getArguments().getString(ARG_LICENSEFILENAME);
            userName = getArguments().getString(ARG_USERNAME);
        }
        enableBlueTooth();
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

    TextView idDetailLockName, idDetailMac, idSoftver, idHdver, idAddr, idMcuver, idMcuverTitle;
    ImageView idLockOwner, idLockManager, idLockUser, idLockPolice, idLockIdcardRealnameNeeded;
    LinearLayout layout_detail;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_national_main, container, false);

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

        layout_detail = view.findViewById(R.id.layout_detail);
        idDetailLockName = view.findViewById(R.id.id_detail_lock_name);
        idDetailMac = view.findViewById(R.id.id_detail_mac);
        idSoftver = view.findViewById(R.id.id_softver);
        idHdver = view.findViewById(R.id.id_hdver);
        idAddr = view.findViewById(R.id.id_addr);
        idMcuver = view.findViewById(R.id.id_mcuver);
        idMcuverTitle = view.findViewById(R.id.id_mcuver_title);
        idLockOwner = view.findViewById(R.id.id_lock_owner);
        idLockManager = view.findViewById(R.id.id_lock_manager);
        idLockUser = view.findViewById(R.id.id_lock_user);
        idLockPolice = view.findViewById(R.id.id_lock_police);
        idLockIdcardRealnameNeeded = view.findViewById(R.id.id_lock_idcard_realname_needed);


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

        if (isLogin) {
            getLockList();
            isLogin = false;
        } else {
            getSysConfig();
        }

        //获取缓存锁列表
//        getOfflineLockList();

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
        layout_detail.setVisibility(View.GONE);
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
                if (deviceDetailEntity != null && deviceDetailEntity.getData() != null) {
                    layout_detail.setVisibility(View.VISIBLE);
                    idDetailLockName.setText(deviceDetailEntity.getData().getLockName());
                    idDetailMac.setText(lock.getMac());
                    idSoftver.setText(getDeviceType(lock));

                    idHdver.setText(getVer(lock.getHdVer()));

                    idAddr.setText(lock.getAddress2());

                    String ownerType = lock.getOwnerType();
                    if (LockOwnerType.O_M.equalsIgnoreCase(ownerType)) {
                        idLockOwner.setVisibility(View.VISIBLE);
                        idLockManager.setVisibility(View.VISIBLE);
                        idLockUser.setVisibility(View.GONE);


                    } else if (LockOwnerType.O.equalsIgnoreCase(ownerType)) {
                        idLockOwner.setVisibility(View.VISIBLE);
                        idLockManager.setVisibility(View.GONE);
                        idLockUser.setVisibility(View.GONE);

                    } else if (LockOwnerType.M.equalsIgnoreCase(ownerType)) {
                        idLockOwner.setVisibility(View.GONE);
                        idLockManager.setVisibility(View.VISIBLE);
                        idLockUser.setVisibility(View.VISIBLE);


                    } else if (LockOwnerType.O_U.equalsIgnoreCase(ownerType)) {
                        idLockOwner.setVisibility(View.VISIBLE);
                        idLockManager.setVisibility(View.GONE);
                        idLockUser.setVisibility(View.VISIBLE);

                    } else {
                        idLockOwner.setVisibility(View.GONE);
                        idLockManager.setVisibility(View.GONE);
                        idLockUser.setVisibility(View.VISIBLE);
                        if (LockOwnerType.V.equalsIgnoreCase(ownerType)) {
                            idLockUser.setImageResource(R.drawable.national_icon_lock_visitor);
                        } else {
                            idLockUser.setImageResource(R.drawable.national_icon_lock_user);

                        }
                    }

                    idLockPolice.setVisibility(View.GONE);

//                    if (("1".equalsIgnoreCase(lock_supervise) || "2".equalsIgnoreCase(lock_supervise))
//                            && "1".equalsIgnoreCase(authIdcardNeedRealName)
//                            && (LockOwnerType.O_M.equalsIgnoreCase(ownerType)
//                            || LockOwnerType.O.equalsIgnoreCase(ownerType)
//                            || LockOwnerType.O_U.equalsIgnoreCase(ownerType)
//                            || LockOwnerType.M.equalsIgnoreCase(ownerType))
//
//                    ) {
//                        binding.idLockPolice.setVisibility(View.VISIBLE);
//                    } else {
//                        binding.idLockPolice.setVisibility(View.GONE);
//                    }
//
//
                    if ("1".equalsIgnoreCase(lock.getAuthIdcardNeedRealName())) {
                        idLockIdcardRealnameNeeded.setVisibility(View.VISIBLE);
                    } else {
                        idLockIdcardRealnameNeeded.setVisibility(View.GONE);
                    }

                    String mcuVer = lock.getMcu();
                    if (!TextUtils.isEmpty(mcuVer) && !mcuVer.equals("0.00")) {
                        idMcuver.setVisibility(View.VISIBLE);
                        idMcuverTitle.setVisibility(View.VISIBLE);
                        idMcuver.setText(mcuVer);
                    } else {
                        idMcuverTitle.setVisibility(View.GONE);
                        idMcuver.setVisibility(View.GONE);
                    }

                    Log.d(TAG, lock.getLockName() + ":" + lock.getOwnerType());

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
                                if (lock.getMac().equals(lockList.get(i).getMac())) {
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

                // Toast.makeText(getActivity(), "设备列表获取失败：" + errorMsg, Toast.LENGTH_LONG).show();
                getOfflineLockList();

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
                //getSysConfig();

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                sliding_drawer.setVisibility(View.GONE);
                bannerView.setVisibility(View.GONE);
                layout_no_lock.setVisibility(View.VISIBLE);
                //getSysConfig();
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
                // Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                getOfflineLockList();

            }
        });

    }

    int request_image = 1;

    public void initAppItem() {


        mList.clear();
        AppItem item = null;
        item = new AppItem();
        item.setAppName(getString(R.string.share_user));
        item.setResId(R.drawable.national_icon_lock_share);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_card));
        item.setResId(R.drawable.national_icon_card_a);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_pwd));
        item.setResId(R.drawable.national_icon_auth_visitor);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_idcard));
        item.setResId(R.drawable.national_icon_auth_idcard);
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
//                    SdkHelper.getInstance().faceInit(getActivity(), new SdkHelper.CallBack() {
//                        @Override
//                        public void onSuccess(String jsonStr) {
//                            Intent intent = new Intent(getActivity(), FaceLivenessExpActivity.class);
//                            intent.putExtra("type", "authIdCard");
//                            startActivityForResult(intent, request_image);
//                        }
//
//                        @Override
//                        public void onError(String errCode, String errMsg) {
//                            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
//                        }
//                    });

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
        item.setResId(R.drawable.national_icon_lock_share);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_card));
        item.setResId(R.drawable.national_icon_card_a);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_pwd));
        item.setResId(R.drawable.national_icon_auth_visitor);
        mList.add(item);

        item = new AppItem();
        item.setAppName(getString(R.string.share_idcard));
        item.setResId(R.drawable.national_icon_auth_idcard);
        mList.add(item);

//        item = new AppItem();
//        item.setAppName(getString(R.string.device_manager));
//        item.setResId(R.drawable.icon_auth_tmp);
//        mList.add(item);


        item = new AppItem();
        item.setAppName(getString(R.string.device_data));
        item.setResId(R.drawable.national_icon_user_guiders);
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
            if (sliding_drawer.isOpened()) {
                sliding_drawer.close();
            }
            Intent intent = new Intent(getActivity(), LockListActivity.class);
            startActivity(intent);
        } else if (id == R.id.image_search) {
            if (sliding_drawer.isOpened()) {
                sliding_drawer.close();
            }
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        } else if (lock != null) {
            if (id == R.id.assignment_take_back) {
                //removeFlg 0:不删除 1:删除
                actionType = 2;
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View layout = inflater.inflate(R.layout.layout_national_dialog_chkbox, null);
                chkBoxDeleteAll = (CheckBox) layout.findViewById(R.id.chk_manager_give_back);//注意这一句
                chkBoxDeleteAll.setChecked(true);
                show2ndConfirmDlg(lock, layout, getString(R.string.auth_get_back_txt));

            } else if (id == R.id.assignment_auth) {
                goNext(LockType.LOCK_DELETE);
            } else if (id == R.id.assignment_give_back) {
                actionType = 1;
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View layout = inflater.inflate(R.layout.layout_national_dialog_chkbox, null);
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
                startActivityForResult(intent, REQUEST_LONG_PWD_LIST);


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
    private static final int REQUEST_LONG_PWD_LIST = 66666;


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
        if (requestCode == REQUEST_LOCK_DELETE &&
                resultCode == -1) {
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

        if (requestCode == REQUEST_LONG_PWD_LIST) {
            if (sliding_drawer.isOpened()) {
                getLockDetail();
            }
        }
//        if (requestCode == request_image && resultCode == -1) {
//            goNext(LockType.LOCK_AUTH_IDCARD);
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "isVisibleToUser");
        }
    }

    private String getDeviceType(LockListEntity.Lock lock) {
        String reuslt = "";

        String lockType = lock.getLockType();

        String lockVer = lock.getLockVer();
        if (TextUtils.isEmpty(lockType)) {
            return lockVer;
        } else {

            byte deviceType = StringUtilBle.hexStringToByte(lockType);
            byte comType = deviceType;
            deviceType &= 0xF8;
            deviceType = (byte) (deviceType >> 3);
            String deviceTypeStr = String.format("%02d", deviceType);
            comType &= 0x7;
            String comTypeStr = String.format("%02d", comType);

            StringBuffer builder = new StringBuffer();
            builder.append(lockVer);

            if ("01".equalsIgnoreCase(comTypeStr)) {
                builder.append(" ( 蓝牙");
            } else if ("02".equalsIgnoreCase(comTypeStr)) {
                builder.append(" ( 网关");
            } else if ("03".equalsIgnoreCase(comTypeStr)) {
                builder.append(" ( NB");
            } else if ("04".equalsIgnoreCase(comTypeStr)) {
                builder.append(" ( WIFI");
            }

            String lockAttribute = lock.getLockAttribute();
            if (!TextUtils.isEmpty(lockAttribute) && lockAttribute.length() == 8) {

                if (!builder.toString().contains("(")) {
                    builder.append(" (");
                }

                if (lockAttribute.substring(7, 8).equalsIgnoreCase("1")) {
                    builder.append(" 密码");
                }
                if (lockAttribute.substring(6, 7).equalsIgnoreCase("1")) {
                    builder.append(" 指纹");
                }
                if (lockAttribute.substring(5, 6).equalsIgnoreCase("1")) {
                    builder.append(" 指静脉");
                }

            }

            if ("00".equalsIgnoreCase(deviceTypeStr)) {
                if (!builder.toString().contains("(")) {
                    builder.append(" (");
                }
                builder.append(" 门锁");

            } else if ("01".equalsIgnoreCase(deviceTypeStr)) {
                if (!builder.toString().contains("(")) {
                    builder.append(" (");
                }

                builder.append(" 门禁");
            } else {

            }
            if (builder.toString().contains("(")) {
                builder.append(" )");
            }

            reuslt = builder.toString();
        }
        return reuslt;
    }

    public static String getVer(String s) {
        String ver = "0.00";
        if (!TextUtils.isEmpty(s) && s.length() == 4) {

            try {
                ver = Integer.valueOf(s.substring(2, 4)) + "." + String.format("%02d", Integer.valueOf(s.substring(0, 2)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return ver;

    }


}