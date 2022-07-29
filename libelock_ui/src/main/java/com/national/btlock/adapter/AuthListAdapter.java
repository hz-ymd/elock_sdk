package com.national.btlock.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.national.btlock.ui.R;
import com.national.btlock.utils.AppConstants;
import com.national.btlock.widget.NoDoubleListener;
import com.national.core.nw.entity.LockAuthListEntity;

import java.text.DecimalFormat;
import java.util.List;

public class AuthListAdapter extends RecyclerView.Adapter<AuthListAdapter.ViewHolder> implements AppConstants {

    private List<LockAuthListEntity.LockAuthItem> mList;
    DecimalFormat decimalFormat;
    IAuthListBtnClickListener mListener;

    String mLockType;

    Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.auth_list_item, parent, false);
        AuthListAdapter.ViewHolder holder = new AuthListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final LockAuthListEntity.LockAuthItem item = mList.get(position);//(File) getItem(position);
        holder.btnCheckout.setText(R.string.txt_del_auth);

        if (LockType.LOCK_SHARE.equalsIgnoreCase(mLockType)
                || LockType.LOCK_SHARE_WX.equalsIgnoreCase(mLockType)


        ) {
            holder.llTargetUserId.setVisibility(View.VISIBLE);
            holder.llUid.setVisibility(View.GONE);
            holder.btnRewrite.setVisibility(View.GONE);
            holder.targetUserId.setText(item.getTargetUserId());


        } else if (LockType.LOCK_AUTH_IDCARD.equalsIgnoreCase(mLockType)
                || LockType.LOCK_AUTH_CARD_A.equalsIgnoreCase(mLockType)
                || LockType.LOCK_AUTH_IDCARD_NFC.equalsIgnoreCase(mLockType)

            // || LockType.LOCK_AUTH_FINGERPRINT.equalsIgnoreCase(mLockType)


        ) {
            holder.btnRewrite.setVisibility(View.GONE);
            holder.llTargetUserId.setVisibility(View.GONE);
            holder.llUid.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(item.getCardAlias())) {
                holder.uid.setText(item.getUid());
            } else {
                holder.uid.setText(item.getCardAlias());
            }

        }

        if (TextUtils.isEmpty(item.getOperaterName())) {
            holder.operaterId.setText(item.getOperaterId());

        } else {
            holder.operaterId.setText(item.getOperaterName());

        }

        String validPeriodStr = item.getValidPeriodStr();

        if (TextUtils.isEmpty(validPeriodStr)) {
            validPeriodStr = "";
        }

        if (validPeriodStr.contains("至")) {
            int index = validPeriodStr.indexOf("至") + 1;
            validPeriodStr = validPeriodStr.substring(0, index) + "\n" + validPeriodStr.substring(index);

        }


        holder.validPeriod.setText(validPeriodStr);


        holder.btnRewrite.setOnClickListener(new NoDoubleListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mListener.action(2, item);
            }


        });
        holder.btnCheckout.setOnClickListener(new NoDoubleListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mListener.action(1, item);
            }
        });

        holder.btnExtend.setOnClickListener(new NoDoubleListener() {

            @Override
            public void onNoDoubleClick(View v) {
                mListener.action(3, item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public AuthListAdapter(Context context, String type, List<LockAuthListEntity.LockAuthItem> list, IAuthListBtnClickListener listener) {
        this.context = context;
        this.mList = list;
        mListener = listener;
        decimalFormat = new DecimalFormat(".00");//
        mLockType = type;

    }


    public interface IAuthListBtnClickListener {
        //btnType 1:添加 2:管理（删除）
        void action(int btnType, LockAuthListEntity.LockAuthItem item);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView targetUserId;
        private TextView uid;
        private TextView validPeriod;
        private TextView operaterId;

        private LinearLayout llTargetUserId;
        private LinearLayout llUid;
        private TextView btnCheckout;
        private TextView btnExtend;

        private ImageView btnRewrite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            targetUserId = (TextView) itemView.findViewById(R.id.id_target_userid);
            uid = (TextView) itemView.findViewById(R.id.id_uid);
//            dnCode = (TextView) convertView.findViewById(R.id.id_dncode);
            validPeriod = (TextView) itemView.findViewById(R.id.id_valid_period_str);
            operaterId = (TextView) itemView.findViewById(R.id.id_operater_id);


            llTargetUserId = (LinearLayout) itemView.findViewById(R.id.ll_target_userid);
            llUid = (LinearLayout) itemView.findViewById(R.id.ll_uid);
//            llDnCode = (LinearLayout) convertView.findViewById(R.id.ll_dncode);

            btnRewrite = (ImageView) itemView.findViewById(R.id.id_rewrite_name);

            btnCheckout = (TextView) itemView.findViewById(R.id.id_list_item_btn_checkout);
            btnExtend = (TextView) itemView.findViewById(R.id.id_list_item_btn_extend);

        }
    }

}