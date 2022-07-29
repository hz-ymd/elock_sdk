package com.national.btlock.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ViewHolder {


    private SparseArray<View> views;
    private View convertView;

    public ViewHolder(ViewGroup parent, LayoutInflater inflater, int layoutId) {
        this.views = new SparseArray<View>();
        this.convertView = inflater.inflate(layoutId, parent, false);
        this.convertView.setTag(this);
    }


    public static ViewHolder getViewHolder(ViewGroup parent, View convertView, LayoutInflater inflater, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(parent, inflater, layoutId);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 得到convertView
     *
     * @return
     */
    public View getConvertView() {
        return convertView;
    }

    /**
     * 根据Id得到view
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

}
