package com.national.btlock.sdk.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.national.btlock.sdk.Constants;
import com.national.btlock.sdk.LoginActivity;
import com.national.btlock.sdk.SdkHelper;
import com.national.btlock.sdk.databinding.FragmentMeBinding;
import com.national.btlock.sdk.utils.PreferencesUtils;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MeViewModel meViewModel =
                new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textMe;
//        meViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        binding.idBtnVerify.setOnClickListener(view ->
                SdkHelper.getInstance().identification(getActivity(), "name", "idcardNo", new SdkHelper.identificationCallBack() {
                    @Override
                    public void identificationSuc() {
                        Toast.makeText(getActivity(), "实名认证成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void identificationError(String errCode, String errMsg) {

                        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();

                    }
                })
        );


        binding.idBtnLoginout.setOnClickListener(view ->
                SdkHelper.getInstance().loginOut(new SdkHelper.CallBack() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        PreferencesUtils.putBoolean(getActivity(), Constants.IS_LOGIN, false);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                    }
                })
        );
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}