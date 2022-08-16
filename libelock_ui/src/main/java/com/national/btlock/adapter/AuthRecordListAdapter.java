package com.national.btlock.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.national.btlock.ui.R;
import com.national.core.nw.entity.AuthRecordListEntity;

import java.util.List;

public class AuthRecordListAdapter extends CCAdapter<AuthRecordListEntity.Record> {

    static String MSG_TXT = "%s[%s]给%s[%s]操作门锁【%s】的【%s】，有效期：%s至%s";

    public AuthRecordListAdapter(Context context, List<AuthRecordListEntity.Record> list) {
        super(context, list, R.layout.national_auth_record_list_item);

    }

    @Override
    public void convert(ViewHolder viewHolder, AuthRecordListEntity.Record item) {


        TextView tvData = viewHolder.getView(R.id.id_message_data);
        TextView tvTime = viewHolder.getView(R.id.id_message_data_time);
        ImageView imgvExpired = viewHolder.getView(R.id.id_expired);

        String operatorName = item.getOperatorName();
        if (TextUtils.isEmpty(operatorName)) {
            operatorName = item.getOperatorUserId();

        }

        String targetName = item.getTargetName();
        if (TextUtils.isEmpty(targetName)) {
            targetName = item.getTargetUserId();
        }

        tvData.setText(String.format(MSG_TXT
                , operatorName, item.getOperatorUserId()
                , targetName, item.getTargetUserId(), item.getLockName()
                , item.getAuthTypeTxt(), item.getAuthStartTime(), item.getAuthEndTime()));
        tvTime.setText("[ " + item.getOperateTime() + " ]");
        if (!TextUtils.isEmpty(item.getExpiredFlg()) && item.getExpiredFlg().equalsIgnoreCase("1")) {
            imgvExpired.setVisibility(View.VISIBLE);
        } else {
            imgvExpired.setVisibility(View.GONE);
        }

    }


}