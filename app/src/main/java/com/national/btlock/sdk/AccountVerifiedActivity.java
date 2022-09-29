package com.national.btlock.sdk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.national.btlock.sdk.utils.PreferencesUtils;

public class AccountVerifiedActivity extends AppCompatActivity {

    EditText edit_name, edit_idcard;

    Button btn_acount_verified;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verified);

        setTitle("实名认证");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_name = findViewById(R.id.edit_name);
        edit_idcard = findViewById(R.id.edit_idcard);
        btn_acount_verified = findViewById(R.id.btn_acount_verified);

        btn_acount_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString();
                String idcard = edit_idcard.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idcard)) {
                    Toast.makeText(AccountVerifiedActivity.this, "请输入姓名和身份证号", Toast.LENGTH_LONG).show();
                    return;
                }
                if (idcard.length() != 15 && idcard.length() != 18) {
                    Toast.makeText(AccountVerifiedActivity.this, "请输入正确的身份证号", Toast.LENGTH_LONG).show();
                    return;
                }
                SdkHelper.getInstance().identification(AccountVerifiedActivity.this, name, idcard,
                        new SdkHelper.identificationCallBack() {
                            @Override
                            public void identificationSuc() {
                                Toast.makeText(AccountVerifiedActivity.this, "实名成功", Toast.LENGTH_LONG).show();
                                PreferencesUtils.putString(AccountVerifiedActivity.this, "isAccountVerified", "1");

                                finish();

                            }

                            @Override
                            public void identificationError(String errCode, String errMsg) {
                                Toast.makeText(AccountVerifiedActivity.this, "实名失败：" + errMsg, Toast.LENGTH_LONG).show();

                            }

                        });
            }
        });


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
