package com.national.btlock.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.national.btlock.ui.R;
import com.national.btlock.utils.AppConstants;
import com.national.core.nw.entity.LockPwdShareListEntity;

import java.util.List;

public class LockPwdLongShareListAdapter extends CCAdapter<LockPwdShareListEntity.LockPwdShareData> {

    public LockPwdLongShareListAdapter(Context context, List<LockPwdShareListEntity.LockPwdShareData> list) {
        super(context, list, R.layout.national_lock_pwd_long_share_list_item);

    }

    @Override
    public void convert(ViewHolder viewHolder, final LockPwdShareListEntity.LockPwdShareData item) {

        TextView targetUserId = viewHolder.getView(R.id.id_target_userid);
        TextView tvLockPwdShare = viewHolder.getView(R.id.id_lock_pwd);
        TextView tvPwdType = viewHolder.getView(R.id.id_pwd_type);
        TextView tvValidPeriod = viewHolder.getView(R.id.id_valid_period);
        TextView tvRemark = viewHolder.getView(R.id.id_remark);


        targetUserId.setText(item.getTargetId());
//        if (!TextUtils.isEmpty(item.getLockPwd()) && item.getLockPwd().length() > 8) {
//            tvLockPwdShare.setText(item.getLockPwd().substring(0, 4) + "****" + item.getLockPwd().substring(8));
//        }

        if (!TextUtils.isEmpty(item.getLockPwd())
                && item.getLockPwd().length() >= 15) {
            tvLockPwdShare.setText(item.getLockPwd().substring(0, 5)
                    + "-" + item.getLockPwd().substring(5, 10)
                    + "-" + item.getLockPwd().substring(10));
        }


        tvRemark.setText(item.getRemark());
        String validPeriod = item.getValidPeriod();

        if (TextUtils.isEmpty(validPeriod)) {

        } else {
            String[] result = validPeriod.split("至");
            if (result != null && result.length == 2) {
                tvValidPeriod.setText(result[0] + "至\n" + result[1]);
            }
        }


        TextView btnDelete = viewHolder.getView(R.id.id_list_item_btn_delete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.action(1, item);
            }
        });

        TextView btnShare = viewHolder.getView(R.id.id_list_item_btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.action(2, item);
            }
        });


        if (AppConstants.LockPwdType.M.equalsIgnoreCase(item.getPwdType())) {
            tvPwdType.setText("一次性密码：");

        } else if (AppConstants.LockPwdType.C.equalsIgnoreCase(item.getPwdType())) {
            tvPwdType.setText("有效期密码：");

        }


    }


    public void setOnClickListener(IBtnOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    IBtnOnClickListener onClickListener;

    public interface IBtnOnClickListener {


        void action(int btnType, LockPwdShareListEntity.LockPwdShareData item);

    }


}