package com.hjq.permissions;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;


import com.national.btlock.ui.R;

import java.util.ArrayList;
import java.util.List;


public class PermissionNameConvert {
    /**
     * 获取权限名称
     */
    public static String getPermissionString(Context context, List<String> permissions) {
        return listToString(permissionsToStrings(context, permissions));
    }

    /**
     * String 列表拼接成一个字符串
     */
    public static String listToString(List<String> hints) {
        if (hints == null || hints.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String text : hints) {
            if (builder.length() == 0) {
                builder.append(text);
            } else {
                builder.append("、")
                        .append(text);
            }
        }
        return builder.toString();
    }

    /**
     * 将权限列表转换成对应名称列表
     */
    @NonNull
    public static List<String> permissionsToStrings(Context context, List<String> permissions) {
        List<String> permissionNames = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Permission.READ_EXTERNAL_STORAGE:
                case Permission.WRITE_EXTERNAL_STORAGE: {
                    String hint = "用于上传、下载图片和更新文件等场景中的读取和写入相册和文件内容，需要申请你的文件读写权限。允许后，你可以随时通过手机系统设置对授权进行管理。";
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.CAMERA: {
                    String hint = context.getString(R.string.common_permission_camera);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.RECORD_AUDIO: {
                    String hint = context.getString(R.string.common_permission_microphone);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.ACCESS_FINE_LOCATION:
                case Permission.ACCESS_COARSE_LOCATION:
                case Permission.ACCESS_BACKGROUND_LOCATION: {
                    String hint;
                    if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) &&
                            !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                        hint = "为了获取用户绑定或使用设备的实际位置，也为了保证手机蓝牙功能的正常，需要您允许获取此设备的位置信息。" +
                                "允许后，你可以随时通过手机系统设置对授权进行管理";
                    } else {
                        hint = "为了获取用户绑定或使用设备的实际位置，也为了保证手机蓝牙功能的正常，需要您允许获取此设备的位置信息。" +
                                "允许后，你可以随时通过手机系统设置对授权进行管理";
                    }
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.BLUETOOTH_SCAN:
                case Permission.BLUETOOTH_CONNECT:
                case Permission.BLUETOOTH_ADVERTISE: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        String hint = "蓝牙扫描，连接需要获取蓝牙权限。允许后，你可以随时通过手机系统设置对授权进行管理。";
                        if (!permissionNames.contains(hint)) {
                            permissionNames.add(hint);
                        }
                    }
                    break;
                }
                case Permission.READ_PHONE_STATE: {
                    String hint = "为保障账号、账号下设备安全，需要申请你的设备信息权限。允许后，你可以随时通过手机系统设置对授权进行管理。";
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.CALL_PHONE:
                case Permission.ADD_VOICEMAIL:
                case Permission.USE_SIP:
                case Permission.READ_PHONE_NUMBERS:
                case Permission.ANSWER_PHONE_CALLS:
                case Permission.GET_ACCOUNTS:
                case Permission.READ_CONTACTS:
                case Permission.WRITE_CONTACTS: {
                    String hint = "为获取手机通讯录列表，需要申请你的通讯录权限。允许后，你可以随时通过手机系统设置对授权进行管理。";
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALENDAR:
                case Permission.WRITE_CALENDAR: {
                    String hint = context.getString(R.string.common_permission_calendar);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.READ_CALL_LOG:
                case Permission.WRITE_CALL_LOG:
//               case Permission.PROCESS_OUTGOING_CALLS: {
//                   String hint = context.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
//                           R.string.common_permission_call_log : R.string.common_permission_phone);
//                   if (!permissionNames.contains(hint)) {
//                       permissionNames.add(hint);
//                   }
//                   break;
//               }
                case Permission.BODY_SENSORS: {
                    String hint = context.getString(R.string.common_permission_sensors);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.ACTIVITY_RECOGNITION: {
                    String hint = context.getString(R.string.common_permission_activity_recognition);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
//               case Permission.ACCESS_MEDIA_LOCATION: {
//                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                       String hint = context.getString(R.string.common_permission_media_location);
//                       if (!permissionNames.contains(hint)) {
//                           permissionNames.add(hint);
//                       }
//                   }
//                   break;
//               }
                case Permission.SEND_SMS:
                case Permission.RECEIVE_SMS:
                case Permission.READ_SMS:
                case Permission.RECEIVE_WAP_PUSH:
                case Permission.RECEIVE_MMS: {
                    String hint = context.getString(R.string.common_permission_sms);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
//               case Permission.MANAGE_EXTERNAL_STORAGE: {
//                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                       String hint = context.getString(R.string.common_permission_manage_storage);
//                       if (!permissionNames.contains(hint)) {
//                           permissionNames.add(hint);
//                       }
//                   }
//                   break;
//               }
                case Permission.REQUEST_INSTALL_PACKAGES: {
                    String hint = context.getString(R.string.common_permission_install);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.SYSTEM_ALERT_WINDOW: {
                    String hint = context.getString(R.string.common_permission_window);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.WRITE_SETTINGS: {
                    String hint = context.getString(R.string.common_permission_setting);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.NOTIFICATION_SERVICE: {
                    String hint = context.getString(R.string.common_permission_notification);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.BIND_NOTIFICATION_LISTENER_SERVICE: {
                    String hint = context.getString(R.string.common_permission_notification_listener);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.PACKAGE_USAGE_STATS: {
                    String hint = context.getString(R.string.common_permission_task);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.SCHEDULE_EXACT_ALARM: {
                    String hint = context.getString(R.string.common_permission_alarm);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.ACCESS_NOTIFICATION_POLICY: {
                    String hint = context.getString(R.string.common_permission_not_disturb);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: {
                    String hint = context.getString(R.string.common_permission_ignore_battery);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                case Permission.BIND_VPN_SERVICE: {
                    String hint = context.getString(R.string.common_permission_vpn);
                    if (!permissionNames.contains(hint)) {
                        permissionNames.add(hint);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return permissionNames;
    }
}