package com.oneul.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.DateTime;
import com.oneul.extra.dbHelper;

import java.util.Objects;

public class StatFragment extends Fragment {


    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View statView = inflater.inflate(R.layout.fragment_stat, container, false);


        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}