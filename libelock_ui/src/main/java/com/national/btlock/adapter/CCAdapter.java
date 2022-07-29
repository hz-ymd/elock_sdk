package com.national.btlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public abstract class CCAdapter<T> extends BaseAdapter  {
    protected Context mContext;
    private LayoutInflater layoutInflater;
    private int layoutId;
    private List<T> mList;


    public interface IBtnClickListener<T> {

        //btnType 1:添加 2:管理（删除）
        void action(int btnType, T device);

    }

    public void setmListener(IBtnClickListener mListener) {
        this.mListener = mListener;
    }

    IBtnClickListener mListener;

    public CCAdapter(Context context, List<T> list, int layoutId) {
        mContext = context;
        mList = list;
        layoutInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    @Override
    public long getItemId(int postion) {
        return postion;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = ViewHolder.getViewHolder(parent, convertView,
                layoutInflater, layoutId);
        convert(viewHolder, mList.get(position));
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder viewHolder, T t);


}
