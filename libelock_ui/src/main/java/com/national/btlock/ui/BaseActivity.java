package com.national.btlock.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.national.btlock.widget.SimpleProgressDialog;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    SimpleProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public void showProgressDialog() {
        pd = new SimpleProgressDialog(BaseActivity.this);
        pd.setCancelable(false);
        pd.show();
    }


    public void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static final String TAG = "NoDoubleListener";
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(view);
        } else {
            Log.w(TAG, "点击过快");
        }
    }

    public abstract void onNoDoubleClick(View v);
}
