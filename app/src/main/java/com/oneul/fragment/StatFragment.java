package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.DBHelper;
import com.oneul.stat.StatAdapter;

import java.text.SimpleDateFormat;

public class StatFragment extends Fragment {
    //    ㄴㄴ 리사이클
    public static PieChart pieChart;
    public static RecyclerView statRecycler;
    public static StatAdapter adapter = new StatAdapter();

    //    ㄴㄴ 기타
    DBHelper dbHelper;

    SwipeRefreshLayout sr_swipeRefresh;

    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //인플레이터
        View statView = inflater.inflate(R.layout.fragment_stat, container, false);

        //년,월 표시
        ((TextView) statView.findViewById(R.id.statDay))
                .setText(new SimpleDateFormat("yyyy년 MM월").format(System.currentTimeMillis()));

//        ㄴㄴ 리사이클
        pieChart = statView.findViewById(R.id.pieChart);
        pieChart.setDescription(null);

        statRecycler = statView.findViewById(R.id.statRecycler);
        statRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        statRecycler.setAdapter(adapter);
        statRecycler.setHasFixedSize(false);
        statRecycler.setNestedScrollingEnabled(false);

        //    ㄴㄴ 기타
        dbHelper = DBHelper.getDB(getContext());
        dbHelper.getStat(MainActivity.showDay, pieChart, statRecycler, adapter);

        sr_swipeRefresh = statView.findViewById(R.id.sr_swipeRefresh);
        sr_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dbHelper.getStat(MainActivity.showDay, pieChart, statRecycler, adapter);
                sr_swipeRefresh.setRefreshing(false);
            }
        });

        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}