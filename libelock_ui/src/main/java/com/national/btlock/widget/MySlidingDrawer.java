package com.national.btlock.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

public class MySlidingDrawer extends SlidingDrawer {
    private static final String TAG = "MySlidingDrawer";
    private int mHandleId = 0;                //抽屉行为控件ID
    private int[] mTouchableIds = null;    //Handle 部分其他控件ID

    public int[] getTouchableIds() {
        return mTouchableIds;
    }

    public void setTouchableIds(int[] mTouchableIds) {
        this.mTouchableIds = mTouchableIds;
    }

    public int getHandleId() {
        return mHandleId;
    }

    public void setHandleId(int mHandleId) {
        this.mHandleId = mHandleId;
    }

    public MySlidingDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySlidingDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * 获取控件的屏幕区域
     */
    public Rect getRectOnScreen(View view) {
        Rect rect = new Rect();
        int[] location = new int[2];
        View parent = view;
        if (view.getParent() instanceof View) {
            parent = (View) view.getParent();
        }
        parent.getLocationOnScreen(location);
        view.getHitRect(rect);
        rect.offset(location[0], location[1]);
        return rect;
    }



    public boolean onInterceptTouchEvent(MotionEvent event) {
        // 触摸位置转换为屏幕坐标
        int[] location = new int[2];
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d(TAG, "x:" + x + ",y:" + y);
        this.getLocationOnScreen(location);
        Log.d(TAG, "location[0]:" + location[0] + ",location[1]:" + location[1]);
        x += location[0];
        y += location[1];
        // handle部分独立按钮
        if (mTouchableIds != null) {
            for (int id : mTouchableIds) {
                View view = findViewById(id);
                Rect rect = getRectOnScreen(view);
                Log.d(TAG, "Touchable" + ":" + rect.toString());

                if (rect.contains(x, y)) {
                    //return
                    Log.d(TAG, "contain:" + id);
//                    view.dispatchTouchEvent(event);
                    return false;
                }
            }
        }

        // 抽屉行为控件
        if (event.getAction() == MotionEvent.ACTION_DOWN && mHandleId != 0) {
            View view = findViewById(mHandleId);
            Rect rect = getRectOnScreen(view);
            Log.d(TAG, "handle" + ":" + rect.toString());
            if (rect.contains(x, y)) {//点击抽屉控件时交由系统处理
                Log.i("MySlidingDrawer", "Hit handle");
            } else {
                return false;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}

