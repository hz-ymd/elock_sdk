package com.national.btlock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.national.btlock.ui.databinding.ActivitySearchBinding;

public class SearchActivity extends BaseActivity {

    ActivitySearchBinding binding;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("搜索");

        binding.edtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    Toast.makeText(SearchActivity.this, "请输入设备名称", Toast.LENGTH_LONG).show();
                }
//                getRepoList(orderType, query);

                goLockList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                key = newText;
                return true;
            }
        });


        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(SearchActivity.this, "请输入设备名称", Toast.LENGTH_LONG).show();
                }
                goLockList(key);
            }
        });
    }

    public void goLockList(String key) {
        Intent intent = new Intent(SearchActivity.this, LockListActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
        finish();
    }

}
