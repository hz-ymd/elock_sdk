package com.national.btlock.adapter;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.national.btlock.ui.R;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.entity.LockListEntity;

import java.util.List;

public class LockListAdapter extends RecyclerView.Adapter<LockListAdapter.ViewHolder> {

    Context context;
    List<LockListEntity.Lock> lockList;
    OnClickListener onClickListener;

    public LockListAdapter(Context context, List<LockListEntity.Lock> lockList, OnClickListener onClickListener) {
        this.context = context;
        this.lockList = lockList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_lock_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LockListEntity.Lock entiy = lockList.get(position);

        holder.lock_address.setText("地址：" + entiy.getAddress2());
        holder.lock_battery.setText("电量：" + entiy.getBattery());
        holder.lock_name.setText(entiy.getLockName());


        holder.text_ver.setText("版本：" + entiy.getLockVer());
        if ("1".equalsIgnoreCase(entiy.getAuthIdcardNeedRealName())) {
            holder.imgvIdcardRealNameNeeded.setVisibility(View.VISIBLE);
        } else {
            holder.imgvIdcardRealNameNeeded.setVisibility(View.GONE);
        }


        if (AppConstants.LockOwnerType.O_M.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.VISIBLE);
            holder.imgvLockManager.setVisibility(View.VISIBLE);
            holder.imgvLockUser.setVisibility(View.GONE);

//            if (LockType.LOCK_DELETE.equalsIgnoreCase(mLockType)) {
//                holder.tvAuthDetail.setVisibility(View.VISIBLE);
//            }

        } else if (AppConstants.LockOwnerType.O.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.VISIBLE);
            holder.imgvLockManager.setVisibility(View.GONE);
            holder.imgvLockUser.setVisibility(View.GONE);
//            if (LockType.LOCK_DELETE.equalsIgnoreCase(mLockType)) {
//                holder.tvAuthDetail.setVisibility(View.VISIBLE);
//            }
        } else if (AppConstants.LockOwnerType.M.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.imgvLockOwner.setVisibility(View.GONE);
            holder.imgvLockManager.setVisibility(View.VISIBLE);
            holder.imgvLockUser.setVisibility(View.VISIBLE);


        } else if (AppConstants.LockOwnerType.O_U.equalsIgnoreCase(entiy.getOwnerType())) {
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

            if (AppConstants.LockOwnerType.V.equalsIgnoreCase(entiy.getOwnerType())) {
//                holder.imgvLockUser.setBackground(R.drawable.icon_lock_visitor);
                holder.imgvLockUser.setImageResource(R.drawable.icon_lock_visitor);
            } else {
//                holder.imgvLockUser.setBackgroundResource(R.drawable.icon_lock_user);
                holder.imgvLockUser.setImageResource(R.drawable.icon_lock_user);

            }
        }

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });

        if (AppConstants.LockOwnerType.O.equalsIgnoreCase(entiy.getOwnerType())) {
            holder.image_open.setVisibility(View.GONE);
        } else {
            holder.image_open.setVisibility(View.VISIBLE);
        }

        holder.image_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.open(position);
                }
            }
        });
        String ownerType = entiy.getOwnerType();
        if (ownerType.equals(AppConstants.LockOwnerType.O) || ownerType.equals(AppConstants.LockOwnerType.O_U) || ownerType.equals(AppConstants.LockOwnerType.O_M)) {
            holder.assignment_auth.setVisibility(View.VISIBLE);
            holder.assignment_auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.assignment(position);

                }
            });
        } else {
            holder.assignment_auth.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return lockList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView lock_name;
        TextView lock_battery;
        TextView lock_address;
        TextView image_open;
        TextView text_expired;

        ImageView imgvLockOwner;
        ImageView imgvLockManager;
        ImageView imgvLockUser;
        ImageView imgvLockPolice;
        ImageView imgvIdcardRealNameNeeded;

        TextView text_ver;

        TextView assignment_auth;

        public ViewHolder(@NonNull View view) {
            super(view);
            lock_address = itemView.findViewById(R.id.lock_address);
            lock_address = view.findViewById(R.id.lock_address);
            lock_name = view.findViewById(R.id.lock_name);
            lock_battery = view.findViewById(R.id.lock_battery);
            image_open = view.findViewById(R.id.image_open);
            text_expired = view.findViewById(R.id.text_expired);
            text_ver = view.findViewById(R.id.text_ver);
            imgvLockOwner = (ImageView) view.findViewById(R.id.id_lock_owner);
            imgvLockManager = (ImageView) view.findViewById(R.id.id_lock_manager);
            imgvLockUser = (ImageView) view.findViewById(R.id.id_lock_user);
            imgvLockPolice = (ImageView) view.findViewById(R.id.id_lock_police);
            imgvIdcardRealNameNeeded = (ImageView) view.findViewById(R.id.id_lock_idcard_realname_needed);
            assignment_auth = view.findViewById(R.id.assignment_auth);
        }
    }


    public interface OnClickListener {
        //btnType 1:添加 2:管理（删除）
        void onClick(int position);

        void open(int position);

        void assignment(int position);
    }

}
