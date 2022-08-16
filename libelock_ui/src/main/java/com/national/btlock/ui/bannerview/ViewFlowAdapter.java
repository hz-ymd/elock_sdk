/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.national.btlock.ui.bannerview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.national.btlock.ui.R;
import com.national.core.SDKCoreHelper;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.entity.LockListEntity;
import com.national.core.nw.it.OnProgressUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 图片适配器
 */
public class ViewFlowAdapter extends BaseAdapter implements AppConstants {

    private Context context;

    private int size;
    private boolean isInfiniteLoop;
    private List<LockListEntity.Lock> mItems = new ArrayList<>();

    public interface OnImageClickLinstener {
        public void onClick(LockListEntity.Lock entiy);
    }

    OnImageClickLinstener mOnImageClickLinstener = null;

    public void setOnImageClickLinstener(OnImageClickLinstener linstener) {
        this.mOnImageClickLinstener = linstener;
    }

    public ViewFlowAdapter(Context context, List<LockListEntity.Lock> items) {
        this.context = context;
        mItems = items;

        if (mItems != null) {
            this.size = mItems.size();
        }

        isInfiniteLoop = false;


    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : mItems.size();


    }

    @Override
    public LockListEntity.Lock getItem(int position) {
        return mItems.get(position);
//        return mItems.get(getPosition(position));
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    public int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup container) {

        final ViewHolder holder;
        if (view == null) {

            view = LayoutInflater.from(container.getContext()).inflate(R.layout.national_item_lock_grid, container, false);
            holder = new ViewHolder();
//            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            view = holder.imageView = new SimpleDraweeView(context);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final LockListEntity.Lock entiy = getItem(position);
        holder.text_open = view.findViewById(R.id.text_open);
        holder.lock_address = view.findViewById(R.id.lock_address);
        holder.lock_name = view.findViewById(R.id.lock_name);
        holder.lock_battery = view.findViewById(R.id.lock_battery);
        holder.image_open = view.findViewById(R.id.image_open);
        holder.text_expired = view.findViewById(R.id.text_expired);
        holder.text_ver = view.findViewById(R.id.text_ver);

        holder.lock_address.setText(entiy.getAddress2());
        holder.lock_battery.setText(entiy.getBattery());
        holder.lock_name.setText(entiy.getLockName());


        holder.imgvLockOwner = (ImageView) view.findViewById(R.id.id_lock_owner);
        holder.imgvLockManager = (ImageView) view.findViewById(R.id.id_lock_manager);
        holder.imgvLockUser = (ImageView) view.findViewById(R.id.id_lock_user);

        holder.imgvLockPolice = (ImageView) view.findViewById(R.id.id_lock_police);


        holder.imgvIdcardRealNameNeeded = (ImageView) view.findViewById(R.id.id_lock_idcard_realname_needed);

        holder.text_ver.setText("版本：" + entiy.getLockVer());
        if ("1".equalsIgnoreCase(entiy.getAuthIdcardNeedRealName())) {
            holder.imgvIdcardRealNameNeeded.setVisibility(View.VISIBLE);
        } else {
            holder.imgvIdcardRealNameNeeded.setVisibility(View.GONE);
        }


        if (LockOwnerType.O_M.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.VISIBLE);
            holder.imgvLockManager.setVisibility(View.VISIBLE);
            holder.imgvLockUser.setVisibility(View.GONE);

//            if (LockType.LOCK_DELETE.equalsIgnoreCase(mLockType)) {
//                holder.tvAuthDetail.setVisibility(View.VISIBLE);
//            }

        } else if (LockOwnerType.O.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.VISIBLE);
            holder.imgvLockManager.setVisibility(View.GONE);
            holder.imgvLockUser.setVisibility(View.GONE);
//            if (LockType.LOCK_DELETE.equalsIgnoreCase(mLockType)) {
//                holder.tvAuthDetail.setVisibility(View.VISIBLE);
//            }
        } else if (LockOwnerType.M.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.GONE);
            holder.imgvLockManager.setVisibility(View.VISIBLE);
            holder.imgvLockUser.setVisibility(View.VISIBLE);


        } else if (LockOwnerType.O_U.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.VISIBLE);
            holder.imgvLockManager.setVisibility(View.GONE);
            holder.imgvLockUser.setVisibility(View.VISIBLE);
//            if (LockType.LOCK_DELETE.equalsIgnoreCase(mLockType)) {
//                holder.tvAuthDetail.setVisibility(View.VISIBLE);
//            }
        } else {
            holder.imgvLockOwner.setVisibility(View.GONE);
            holder.imgvLockManager.setVisibility(View.GONE);
            holder.imgvLockUser.setVisibility(View.VISIBLE);

            if (LockOwnerType.V.equalsIgnoreCase(entiy.getOwnerType())) {
//                holder.imgvLockUser.setBackground(R.drawable.icon_lock_visitor);
                holder.imgvLockUser.setImageResource(R.drawable.national_icon_lock_visitor);
            } else {
//                holder.imgvLockUser.setBackgroundResource(R.drawable.icon_lock_user);
                holder.imgvLockUser.setImageResource(R.drawable.national_icon_lock_user);

            }

        }


//        if ("1".equalsIgnoreCase(entiy.getAuthIdcardNeedRealName())
//                && ("1".equalsIgnoreCase(entiy.getSupervise()) || "2".equalsIgnoreCase(entiy.getSupervise()))
//                && (LockOwnerType.O_M.equalsIgnoreCase(entiy.getOwnerType())
//                || LockOwnerType.O.equalsIgnoreCase(entiy.getOwnerType())
//                || LockOwnerType.O_U.equalsIgnoreCase(entiy.getOwnerType())
//                || LockOwnerType.M.equalsIgnoreCase(entiy.getOwnerType()))
//
//        ) {
//            holder.imgvLockPolice.setVisibility(View.VISIBLE);
//        } else {
//            holder.imgvLockPolice.setVisibility(View.GONE);
//        }

        String validData = entiy.getValidPeriodStr();
        holder.text_expired.setTextColor(Color.parseColor("#000000"));
        String[] validrr = validData.split("至");

        if (validrr != null && validrr.length == 2) {
//            holder.tvValidPeriodStr.setText("至" + validrr[1]);
            holder.text_expired.setText("有效期至：" + validrr[1]);

//            if (DateTimeUtil.datetimeCompare(validrr[1], endDate) >= 0) {
//                holder.text_expired.setTextColor(Color.parseColor("#fa5f49"));
//            } else {
////                holder.tvValidPeriodStr.setTextColor(Color.parseColor("#000000"));
//            }
        } else {
            holder.text_expired.setText("有效期至：" + entiy.getValidPeriodStr());
        }
        holder.image_open.setProgress(0.9f);
        holder.image_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.image_open.setRepeatCount(-1);
                holder.image_open.playAnimation();
                SDKCoreHelper.openLock(entiy.getMac(), new OnProgressUpdateListener() {
                    @Override
                    public void onProgressUpdate(String message) {
                        holder.text_open.setText(message);
//                        Log.d(TAG, "onProgressUpdate:" + message);
                    }

                    @Override
                    public void onSuccess(String jsonStr) {
                        holder.image_open.cancelAnimation();
                        holder.text_open.setText("点击上方开锁");
                        holder.image_open.setProgress(0.9f);
                        Toast.makeText(context, "开门成功", Toast.LENGTH_LONG).show();
                        //Log.d(TAG, "onSuccess:" + jsonStr);
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        holder.image_open.cancelAnimation();
                        holder.text_open.setText("点击上方开锁");
                        holder.image_open.setProgress(0.9f);
                        Toast.makeText(context, "开门失败:" + errorMsg, Toast.LENGTH_LONG).show();
                        // Log.d(TAG, "onError:" + errorCode + "," + errorMsg);
                    }
                });
//                mOnImageClickLinstener.onClick(entiy);
            }
        });


        String battery = entiy.getBattery();
        holder.lock_battery.setTextColor(Color.parseColor("#000000"));
        if (battery != null && battery.contains("%")) {
            battery = battery.substring(0, battery.indexOf("%"));
            if (!TextUtils.isEmpty(battery)) {
                int batt = Integer.valueOf(battery);

                if (batt <= 10) {
                    holder.lock_battery.setTextColor(Color.parseColor("#fa5f49"));//#ff0000
                }

            }

        }


        return view;
    }

    //	private static class ViewHolder {
    private class ViewHolder {
        //        ImageView imageView;
        TextView lock_name;
        TextView lock_battery;
        TextView lock_address;
        LottieAnimationView image_open;
        TextView text_expired;

        ImageView imgvLockOwner;
        ImageView imgvLockManager;
        ImageView imgvLockUser;
        ImageView imgvLockPolice;
        ImageView imgvIdcardRealNameNeeded;

        TextView text_ver;

        TextView text_open;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ViewFlowAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }


    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

}
