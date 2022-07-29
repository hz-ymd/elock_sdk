# elock_sdk
SDK接入流程
-
1.用户移动应用注册

客户在e锁开放平台创建相关移动应用，平台会生成AppID和AppSecret，用于校验用户身份有效性。

创建应用需要填入下列关键信息：
| 平台        | 关键信息   |  备注  |
| --------   | -----:  | :----: |
| Android平台 | 应用签名MD5/包名   |   用于用户合法性校验    |
| iOS平台     | Bundle ID     |   用于用户合法性校验    |
| 小程序       |AppID          |   用于用户合法性校验    |

2.实名人脸采集方案第三方申请

详情见“实名人脸采集方案第三方申请”文档

3.使用sdk

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
	...
	maven { url 'https://jitpack.io' }
		}
}
```
Add the dependency

```
dependencies {
          //百度人脸库，如另外有引用可不添加
          implementation 'com.github.hz-ymd:baiduFaceLib:1.0.0'
          //第三方库，dfu升级库，gson库，ormlite库，eventbus库，
          //如另外有引用，可忽略或不引用
          implementation 'com.github.hz-ymd:CommonLibs:1.0.0'
	  implementation 'com.github.hz-ymd:elock_sdk:0.0.5'
	}
```
权限声明
```
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> 
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> 
<uses-permission android:name="android.permission.CAMERA" />
```

类声明

```
        <uses-library
            android:name="org.apache.http.legacy"
            android:required="false" />

        <activity android:name="com.national.btlock.ui.face.FaceLivenessExpActivity"></activity>

        <service android:name="com.national.core.bt.service.BleServiceM"></service>

        <service android:name="com.national.core.update.CheckLockUpdateService"></service>

        <activity
            android:name="com.national.btlock.ui.LockShareActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockListActivity"
            android:launchMode="singleTask"
            android:theme="@style/Theme.ElockSdk_NoActionBar"></activity>

        <activity
            android:name="com.national.btlock.ui.AuthListActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockShareExtendActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockDetailActivity"
            android:launchMode="singleTask"></activity>

        <activity

            android:name="com.national.btlock.ui.SearchActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.BleComunicationInfoActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ocr.ui.camera.CameraActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockPwdShareActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockOpenRecordListActivity"
            android:launchMode="singleTask"></activity>
        <activity
            android:name="com.national.btlock.ui.OperateRecordActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.AuthRecordListActivity"
            android:launchMode="singleTask"></activity>

        <activity
            android:name="com.national.btlock.ui.LockPwdLongShareListActivity"
            android:launchMode="singleTask">
        </activity>
```


4.接入使用
从百度申请下载授权文件license，复制到app/src/main/assets目录下

4.1sdk初始化
| 参数        | 备注   | 
| --------   | -----:  | 
| context | 上下文   
| appId | sdk申请appId   
| appSecret | sdk申请appSecret  
| licenseId | 百度申请licenseId   
| licenseFileName |百度配置文件名称 


```
        SdkHelper.getInstance().init(context,
                appId,
               appSecret,
               licenseId,
               licenseFileName,
                new SdkHelper.initCallBack() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG, "initSuccess");

                    }

                    @Override
                    public void initFailure(int errCode, String errMsg) {
                        Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                    }


        });
```
4.2登录

| 参数        | 备注   | 
| --------   | -----:  | 
| context | 上下文  
| username | 用户手机号   

```
SdkHelper.getInstance().login(context,username, new SdkHelper.CallBack() {
                @Override
                public void onSuccess(String jsonStr) {
                    loading.setVisibility(View.GONE);
                    Log.d(TAG, "jsonStr:" + jsonStr);
                    LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                    if (result != null) {
                        if (result.getData() != null) {
                            String isAccountVerified = result.getData().getAccountVerified();
                            PreferencesUtils.putBoolean(LoginActivity.this, IS_LOGIN, true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("isAccountVerified", isAccountVerified);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    loading.setVisibility(View.GONE);
                    Log.d(TAG, "error:" + errorCode + ",errorMsg:" + errorMsg);
                }
            });
        });
```
4.3 退出登录
```
SdkHelper.getInstance().loginOut(new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        PreferencesUtils.putBoolean(getActivity(), Constants.IS_LOGIN, false);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }
                })
 ```
 4.4 实名认证
 | 参数        | 备注   | 
| --------   | -----:  | 
| context       | 上下文
| name       | 姓名
| idcardNo   | 身份证号
```
SdkHelper.getInstance().identification(context, name, idcardNo, new SdkHelper.identificationCallBack() {
                    @Override
                    public void identificationSuc() {
                        Toast.makeText(getActivity(), "实名认证成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void identificationError(String errCode, String errMsg) {

                        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();

                    }
                })
 ```     
4.5 首页
将com.national.btlock.ui.MainFragment集成到对应页面中

4.6 其他设备登录监听

```
 SdkHelper.getInstance().loginListener(MainActivity.this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SdkHelper.getInstance().loginOut(new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        PreferencesUtils.putBoolean(MainActivity.this, Constants.IS_LOGIN, false);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errCode, String errMsg) {

                    }
                });
            }
        });
  ```
                
                

