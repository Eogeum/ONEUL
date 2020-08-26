package com.oneul.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;

public class StatFragment extends Fragment {

    Button statLeft, statRight;
    TextView statMonth;

    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View statView = inflater.inflate(R.layout.fragment_stat, container, false);

        statLeft = statView.findViewById(R.id.statLeft);
        statRight = statView.findViewById(R.id.statRight);
        statMonth = statView.findViewById(R.id.statMonth);

        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}