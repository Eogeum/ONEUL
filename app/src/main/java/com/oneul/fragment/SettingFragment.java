package com.oneul.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.oneul.service.RealService;

import java.util.Objects;

public class SettingFragment extends Fragment {
    SharedPreferences preferences;

    SwitchCompat switch_fixNoti;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        인플레이터 관련
        final View settingView = inflater.inflate(R.layout.fragment_setting, container, false);

        preferences = Objects.requireNonNull(getContext()).getSharedPreferences("sFile", Context.MODE_PRIVATE);

//        ㄴㄴ 뷰
        switch_fixNoti = settingView.findViewById(R.id.switch_fixNoti);
        switch_fixNoti.setChecked(RealService.fixNoti);
        switch_fixNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RealService.fixNoti = isChecked;
                preferences.edit().putBoolean("fixNoti", isChecked).apply();

                if (isChecked) {
                    RealService.startForeground(getContext());
                } else {
                    RealService.stopForeground(getContext());
                }
            }
        });

        return settingView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}