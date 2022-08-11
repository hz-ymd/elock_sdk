package com.national.btlock.sdk;


import static com.national.btlock.utils.AppConstants.ACTION_LOGIN_AGAIN;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.listener.IInitCallback;
import com.baidu.idl.main.facesdk.statistic.NetWorkUtil;
import com.national.btlock.ui.face.FaceLivenessExpActivity;
import com.national.btlock.ui.face.manager.QualityConfigManager;
import com.national.btlock.ui.face.model.QualityConfig;
import com.national.btlock.utils.MD5;
import com.national.core.SDKCoreHelper;
import com.national.core.nw.it.OnResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author xuliang
 */
public class SdkHelper {
    private static SdkHelper sInstance;

    public static SdkHelper getInstance() {
        if (sInstance == null) {
            sInstance = new SdkHelper();
        }
        return sInstance;
    }

    String appID;
    String appSecret;
    Context context;

    /**
     * sdk初始化
     *
     * @param context
     * @param appID           sdk申请appid
     * @param appSecret       sdk申请appSecret
     * @param licenseId       百度licenseId
     * @param licenseFileName 百度配置文件名称
     * @param callBack        初始化回调
     */
    public void init(Context context, String appID,
                     String appSecret, String licenseId,
                     String licenseFileName, initCallBack callBack) {

        this.context = context;
        this.appID = appID;
        this.appSecret = appSecret;

        SDKCoreHelper.init(context);


        if (!setFaceConfig()) {
            callBack.initFailure("-6000008", "初始化失败 = json配置文件解析出错");
            return;
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(context, licenseId, licenseFileName, new IInitCallback() {
            @Override
            public void initSuccess() {
                callBack.initSuccess();
            }

            @Override
            public void initFailure(final int errCode, final String errMsg) {
                callBack.initFailure(errCode + "", errMsg);

            }
        });

    }


    /**
     * sdk初始化（不初始化人脸识别）
     *
     * @param context
     * @param appID     sdk申请appid
     * @param appSecret sdk申请appSecret
     * @param callBack  初始化回调
     */
    public void init(Context context, String appID,
                     String appSecret, initCallBack callBack) {

        this.context = context;
        this.appID = appID;
        this.appSecret = appSecret;

        SDKCoreHelper.init(context, new OnResultListener() {
            @Override
            public void onSuccess(String jsonStr) {
                callBack.initSuccess();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                callBack.initFailure(errorCode, errorMsg);

            }
        });


    }


    public static List<LivenessTypeEnum> livenessList = new ArrayList<>();

    private boolean setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级

        int qualityLevel = 0;

        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(context.getApplicationContext(), qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        if (qualityConfig == null) {
            return false;
        }
        // 设置模糊度阈值
        config.setBlurnessValue(qualityConfig.getBlur());
        // 设置最小光照阈值（范围0-255）
        config.setBrightnessValue(qualityConfig.getMinIllum());
        // 设置最大光照阈值（范围0-255）
        config.setBrightnessMaxValue(qualityConfig.getMaxIllum());
        // 设置左眼遮挡阈值
        config.setOcclusionLeftEyeValue(qualityConfig.getLeftEyeOcclusion());
        // 设置右眼遮挡阈值
        config.setOcclusionRightEyeValue(qualityConfig.getRightEyeOcclusion());
        // 设置鼻子遮挡阈值
        config.setOcclusionNoseValue(qualityConfig.getNoseOcclusion());
        // 设置嘴巴遮挡阈值
        config.setOcclusionMouthValue(qualityConfig.getMouseOcclusion());
        // 设置左脸颊遮挡阈值
        config.setOcclusionLeftContourValue(qualityConfig.getLeftContourOcclusion());
        // 设置右脸颊遮挡阈值
        config.setOcclusionRightContourValue(qualityConfig.getRightContourOcclusion());
        // 设置下巴遮挡阈值
        config.setOcclusionChinValue(qualityConfig.getChinOcclusion());
        // 设置人脸姿态角阈值
        config.setHeadPitchValue(qualityConfig.getPitch());
        config.setHeadYawValue(qualityConfig.getYaw());
        config.setHeadRollValue(qualityConfig.getRoll());
        // 设置可检测的最小人脸阈值
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 设置可检测到人脸的阈值
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 设置闭眼阈值
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // 设置图片缓存数量
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        livenessList.add(LivenessTypeEnum.Eye);
        livenessList.add(LivenessTypeEnum.Mouth);
        config.setLivenessTypeList(livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(true);
        // 设置开启提示音
        config.setSound(true);
        // 原图缩放系数
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        config.setCropWidth(FaceEnvironment.VALUE_CROP_WIDTH);
        // 抠图人脸框与背景比例
        config.setEnlargeRatio(FaceEnvironment.VALUE_CROP_ENLARGERATIO);
        // 检测超时设置
        config.setTimeDetectModule(FaceEnvironment.TIME_DETECT_MODULE);
        // 检测框远近比率
        config.setFaceFarRatio(FaceEnvironment.VALUE_FAR_RATIO);
        config.setFaceClosedRatio(FaceEnvironment.VALUE_CLOSED_RATIO);
        FaceSDKManager.getInstance().setFaceConfig(config);
        return true;
    }


    public interface initCallBack {
        void initSuccess();

        void initFailure(final String errCode, final String errMsg);
    }


    public interface identificationCallBack {
        void identificationSuc();

        void identificationError(final String errCode, final String errMsg);
    }


    identificationCallBack callBack;

    public void setCallBack(identificationCallBack callBack) {
        this.callBack = callBack;
    }

    public identificationCallBack getCallBack() {
        return callBack;
    }


    CallBack loginCallBack;

    public void setLoginCallBack(CallBack callBack) {
        this.loginCallBack = callBack;
    }

    public CallBack getLoginCallBack() {
        return loginCallBack;
    }

    /**
     * 实名认证（添加人脸初始化）
     *
     * @param context
     * @param name     姓名
     * @param idCardNo 身份证号
     * @param callBack
     */
    public void identification(Context context, String licenseId,
                               String licenseFileName,
                               String name,
                               String idCardNo,
                               identificationCallBack callBack) {

        if (!setFaceConfig()) {
            callBack.identificationError("-6000008", "初始化失败 = json配置文件解析出错");
            return;
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(context, licenseId, licenseFileName, new IInitCallback() {
            @Override
            public void initSuccess() {
                setCallBack(callBack);
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idCardNo)) {
                    callBack.identificationError("100001", "请输入姓名和身份证号");
                    return;
                }
                Intent intent = new Intent(context, FaceLivenessExpActivity.class);
                intent.putExtra("type", "identification");
                intent.putExtra("name", name);
                intent.putExtra("idCardNo", idCardNo);
                context.startActivity(intent);
            }

            @Override
            public void initFailure(final int errCode, final String errMsg) {
                callBack.identificationError("-6000009", "初始化失败：" + errMsg);

            }
        });

    }


    public interface CallBack {
        void onSuccess(String jsonStr);

        void onError(final String errCode, final String errMsg);
    }

    /**
     * 退出登录
     */
    public void loginOut(CallBack callBack) {
        SDKCoreHelper.loginOut(new OnResultListener() {
            @Override
            public void onSuccess(String s) {
                callBack.onSuccess(s);
            }

            @Override
            public void onError(String s, String s1) {
                callBack.onError(s, s1);

            }
        });
    }

    public interface LoginCallBack {
        void onSuccess(String jsonStr);

        void onError(final String errCode, final String errMsg);

        void onFaceCheck();
    }


    /**
     * 登录（旧接口，onError时需判断errorCode是否为LOGIN_IN_OTHER_DEVICE，进行刷脸登录）
     *
     * @param userName 手机账号
     * @param callBack
     */
    public void login(Context context, String userName, CallBack callBack) {
        if (NetWorkUtil.getNetworkState(context) == 0) {
            SDKCoreHelper.getLoginState(userName, new OnResultListener() {
                @Override
                public void onSuccess(String s) {
                    callBack.onSuccess(s);
                }

                @Override
                public void onError(String s, String s1) {
                    callBack.onError(s, s1);
                }
            });

        } else {
            String time = "" + System.currentTimeMillis();
            String sign = MD5.md5(appSecret + time);
            SDKCoreHelper.login(appID, userName, getNum(8) + "", time, sign, new OnResultListener() {
                @Override
                public void onSuccess(String jsonStr) {

                    callBack.onSuccess(jsonStr);
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    callBack.onError(errorCode, errorMsg);
                }
            });
        }


    }


    /**
     * 登录
     *
     * @param userName 手机账号
     * @param callBack
     */
    public void login(Context context, String userName, LoginCallBack callBack) {
        if (NetWorkUtil.getNetworkState(context) == 0) {
            SDKCoreHelper.getLoginState(userName, new OnResultListener() {
                @Override
                public void onSuccess(String s) {
                    callBack.onSuccess(s);
                }

                @Override
                public void onError(String s, String s1) {
                    callBack.onError(s, s1);
                }
            });

        } else {
            String time = "" + System.currentTimeMillis();
            String sign = MD5.md5(appSecret + time);
            SDKCoreHelper.login(appID, userName, getNum(8) + "", time, sign, new OnResultListener() {
                @Override
                public void onSuccess(String jsonStr) {

                    callBack.onSuccess(jsonStr);
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    if (errorCode.equals(LOGIN_IN_OTHER_DEVICE)) {
                        callBack.onFaceCheck();
                    } else {
                        callBack.onError(errorCode, errorMsg);
                    }
                }
            });
        }


    }

    public static final String LOGIN_IN_OTHER_DEVICE = "6100010";
    private static final String FACE_INIT_FAIL_JSON_ERROR = "-6000008";
    private static final String FACE_INIT_FAIL = "-6000009";


    public void faceInit(Context context, String licenseId, String licenseFileName, CallBack callBack) {
        if (!setFaceConfig()) {
            callBack.onError(FACE_INIT_FAIL_JSON_ERROR, "初始化失败 = json配置文件解析出错");
            return;
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(context, licenseId, licenseFileName, new IInitCallback() {
            @Override
            public void initSuccess() {
                callBack.onSuccess("");
            }

            @Override
            public void initFailure(final int errCode, final String errMsg) {
                callBack.onError(FACE_INIT_FAIL, "初始化失败：" + errMsg);

            }
        });
    }

    /**
     * 人脸登录（在其他设备登录后调用）
     *
     * @param context
     * @param licenseId       百度licenseId
     * @param licenseFileName 百度文件名称
     * @param callBack
     */
    public void faceLogin(Context context, String licenseId, String licenseFileName, CallBack callBack) {
        faceInit(context, licenseId, licenseFileName, new CallBack() {
            @Override
            public void onSuccess(String jsonStr) {
                setLoginCallBack(callBack);
                Intent intent = new Intent(context, FaceLivenessExpActivity.class);
                intent.putExtra("type", "loginFaceCheck");
                context.startActivity(intent);
            }

            @Override
            public void onError(String errCode, String errMsg) {
                callBack.onError(errCode, errMsg);

            }
        });


    }


    public static long getNum(int digit) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < digit; i++) {
            if (i == 0 && digit > 1) {
                str.append(new Random().nextInt(9) + 1);
            } else {
                str.append(new Random().nextInt(10));
            }
        }
        return Long.parseLong(str.toString());
    }


    public void loginListener(Context context, BroadcastReceiver mReceiver) {
        IntentFilter filterLoginAgain = new IntentFilter();
        filterLoginAgain.addAction(ACTION_LOGIN_AGAIN);
        context.registerReceiver(mReceiver, filterLoginAgain);
    }


}
