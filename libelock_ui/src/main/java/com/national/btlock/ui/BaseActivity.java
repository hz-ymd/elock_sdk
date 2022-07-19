package com.national.btlock.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.national.btlock.widget.SimpleProgressDialog;

public class BaseActivity extends AppCompatActivity {
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
}
