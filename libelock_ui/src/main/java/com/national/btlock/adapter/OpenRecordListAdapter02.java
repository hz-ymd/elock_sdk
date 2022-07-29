package com.national.btlock.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;


import com.national.btlock.ui.R;
import com.national.core.nw.entity.OpenRecordListEntity;

import java.util.List;

public class OpenRecordListAdapter02 extends CCAdapter<OpenRecordListEntity.OpenRecord> {

    public OpenRecordListAdapter02(Context context, List<OpenRecordListEntity.OpenRecord> list) {
        super(context, list, R.layout.open_record_list_item);

    }

    @Override
    public void convert(ViewHolder viewHolder, OpenRecordListEntity.OpenRecord item) {
        TextView tvLockName = viewHolder.getView(R.id.id_open_record_item_lock_name);
        TextView tvUserName = viewHolder.getView(R.id.id_open_record_item_user_name);
        TextView tvOpenTime = viewHolder.getView(R.id.id_open_record_item_open_time);
        TextView tvOpenMethod = viewHolder.getView(R.id.id_open_record_item_open_method);

        tvLockName.setText(item.getLockName());
        if (!TextUtils.isEmpty(item.getUserNameStr())) {
            tvUserName.setText(item.getUserNameStr());
        } else {
            tvUserName.setText(item.getUserNameStr());
        }

        tvOpenTime.setText(item.getOpenTime());
        tvOpenMethod.setText(item.getOpenMethod());

    }


}