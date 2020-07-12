package com.oneul.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.oneul.R;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(String param1) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        인플레이터 관련
        final View settingView = inflater.inflate(R.layout.fragment_setting, container, false);

        return settingView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

//    todo: 설정 기능, 설정 저장장
}
