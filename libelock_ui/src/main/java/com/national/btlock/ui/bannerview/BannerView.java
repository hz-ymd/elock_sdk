package com.national.btlock.ui.bannerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.national.btlock.ui.R;
import com.national.core.nw.entity.LockListEntity;

import java.util.ArrayList;

public class BannerView extends RelativeLayout {

    private ViewFlow mViewFlow;
    private CircleFlowIndicator mFlowIndicator;
    private Context context;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.layout_banner, this, true);
        mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
        mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
    }

    public int getPosition() {
        return mViewFlow.getSelectedItemPosition();
    }


    ViewFlowAdapter adapter;

    public interface OnChangeListener {
        void onChange(int position);
    }


    public void showBanner(ArrayList<LockListEntity.Lock> items, boolean autoFlow, int selection, ViewFlowAdapter.OnImageClickLinstener linstener, OnChangeListener changeListener) {

        ViewFlowAdapter adapter = new ViewFlowAdapter(context, items);
//		if (items.size() == 1) {
//			adapter.setInfiniteLoop(false);
//		} else {
//			adapter.setInfiniteLoop(true);
//		}

        adapter.setInfiniteLoop(false);

        adapter.setOnImageClickLinstener(linstener);
        mViewFlow.setAdapter(adapter);

        mViewFlow.setOnViewSwitchListener(new ViewFlow.ViewSwitchListener() {
            @Override
            public void onSwitched(View view, int position) {
                changeListener.onChange(position);
            }
        });
        mViewFlow.setmSideBuffer(items.size()); // 实际图片张数
        mFlowIndicator.requestLayout();

        mViewFlow.setFlowIndicator(mFlowIndicator);
        mViewFlow.setTimeSpan(5000);
        mViewFlow.setSelection(selection);
//        if (items.size() == 1) {
//            mViewFlow.setSelection(0); // 设置初始位置
//        } else {
//            mViewFlow.setSelection(items.size() * 1000); // 设置初始位置
//        }
        if (autoFlow) {
            mViewFlow.startAutoFlowTimer(); // 启动自动播放
        }

    }

    // public void showBanner(ArrayList<BannerEntity> items,
    // OnImageClickLinstener linstener) {
    //
    // ViewFlowAdapter adapter = new ViewFlowAdapter(context, items);
    // adapter.setInfiniteLoop(true);
    // adapter.setOnImageClickLinstener(linstener);
    // // mViewFlow.setAdapter(new ViewFlowAdapter(context, items
    // // ).setInfiniteLoop(true));
    // mViewFlow.setAdapter(adapter);
    //
    // mViewFlow.setmSideBuffer(items.size()); // 实际图片张数，
    // mFlowIndicator.requestLayout();
    //
    // mViewFlow.setFlowIndicator(mFlowIndicator);
    // mViewFlow.setTimeSpan(5000);
    // mViewFlow.setSelection(items.size() * 1000); // 设置初始位置
    // // mViewFlow.setSelection(0); // 设置初始位置
    //
    // mViewFlow.startAutoFlowTimer(); // 启动自动播放
    // }

}
