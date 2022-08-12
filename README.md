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
          implementation 'com.j256.ormlite:ormlite-android:5.1'
          implementation 'com.google.code.gson:gson:2.8.2'
          implementation 'com.github.hz-ymd:eventBus:1.0.1'
          implementation 'no.nordicsemi.android:dfu:1.9.1'
          implementation 'com.github.hz-ymd:CommonLibs:1.0.4'
          implementation 'com.github.hz-ymd:libdfu:1.0.1'
	      implementation 'com.github.hz-ymd:elock_sdk:0.0.7'
	}
```

Tips：测试版本sdk使用http请求，请添加network_config.xml文件，并在manifest文件配置
```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```


4.接入使用
从百度申请下载授权文件license，复制到app/src/main/assets目录下

4.1.sdk初始化
Tips：初始化请在Application或者集成com.national.btlock.ui.MainFragment的页面中完成
| 参数        | 备注   | 
| --------   | -----:  | 
| context | 上下文   
| appId | sdk申请appId   
| appSecret | sdk申请appSecret

```
 SdkHelper.getInstance().init(this,
                Constants.APPID,
                Constants.APPSECRET,

                new SdkHelper.initCallBack() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG, "initSuccess");

                    }

                    @Override
                    public void initFailure(String errCode, String errMsg) {
                        Log.d(TAG, "initFailure:" + errCode + "," + errMsg);
                    }


        });
```
4.2登录

| 参数        | 备注   | 
| --------   | -----:  | 
| context | 上下文  
| username | 用户手机号
| LoginCallBack|登录返回回调

```
//登录
SdkHelper.getInstance().login(BaseActivity.this, PreferencesUtils.getString(BaseActivity.this, Constants.USER_NAME), new SdkHelper.LoginCallBack() {
            @Override
            public void onSuccess(String jsonStr) {
                Log.d(TAG, "jsonStr:" + jsonStr);
                LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                if (result != null) {
                    if (result.getData() != null) {
                       //登录成功，返回是否已实名 
                        String isAccountVerified = result.getData().getAccountVerified();
                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                        intent.putExtra("isAccountVerified", isAccountVerified);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                }
            }
          
           //在其他设备登录，需刷脸登录
            @Override
            public void onFaceCheck() {
                SdkHelper.getInstance().faceLogin(BaseActivity.this, Constants.LICENSEID, Constants.LICENSEFILE_NAME, new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                        if (result != null) {
                            if (result.getData() != null) {
                                String isAccountVerified = result.getData().getAccountVerified();
                                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                                intent.putExtra("isAccountVerified", isAccountVerified);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String errCode, String errMsg) {

                    }
                });

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(BaseActivity.this, "登录失败：" + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
```

4.3 刷脸登录
| 参数        | 备注   |
| --------   | -----:  |
| context | 上下文  
| licenseId | 百度licenseId
| licensefileName|百度文件名称

```
SdkHelper.getInstance().faceLogin(BaseActivity.this, Constants.LICENSEID, Constants.LICENSEFILE_NAME, new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        LoginResult result = new Gson().fromJson(jsonStr, LoginResult.class);
                        if (result != null) {
                            if (result.getData() != null) {
                                String isAccountVerified = result.getData().getAccountVerified();
                                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                                intent.putExtra("isAccountVerified", isAccountVerified);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(BaseActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(String errCode, String errMsg) {

                    }
                });
```



4.4 退出登录
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
 4.5 实名认证
 | 参数        | 备注   | 
| --------   | -----:  | 
| context       | 上下文
 |licenseId | 百度licenseId
 | licensefileName|百度文件名称
| name       | 姓名
| idcardNo   | 身份证号 

```
SdkHelper.getInstance().identification(context,Constants.LICENSEID, Constants.LICENSEFILE_NAME,  name, idcardNo, new SdkHelper.identificationCallBack() {
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
4.6 首页
将com.national.btlock.ui.MainFragment集成到对应页面中

4.7 其他设备登录监听

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
                
                

