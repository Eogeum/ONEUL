package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.oneul.R;
import com.oneul.stat.Stat;
import com.oneul.stat.StatAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatFragment extends Fragment {
    PieChart pieChart;

    //    ㄴㄴ 리사이클
    RecyclerView statRecycler;
    ArrayList<Stat> items;

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
//        todo 홈에서 날짜 바구면 바뀌게
        ((TextView) statView.findViewById(R.id.statDay))
                .setText(new SimpleDateFormat("yyyy년 MM월").format(System.currentTimeMillis()));

        //파이차트 (그래프형식)
        ArrayList<PieEntry> pieValue = new ArrayList<>();
        pieValue.add(new PieEntry(31, "코딩"));
        pieValue.add(new PieEntry(24, "강의"));
        pieValue.add(new PieEntry(15, "여가"));
        pieValue.add(new PieEntry(11, "운동"));
        pieValue.add(new PieEntry(8, "독서"));
        PieDataSet dataSet = new PieDataSet(pieValue, "일과 카테고리");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setColor(Color.parseColor("#E88346"));

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10);

        pieChart = statView.findViewById(R.id.pieChart);
        pieChart.setDescription(null);
        pieChart.setData(data);

        //임시 데이터
        items = new ArrayList<>();
        items.clear();
        items.add(new Stat("코딩", "31시간"));
        items.add(new Stat("강의", "24시간"));
        items.add(new Stat("여가", "15시간"));
        items.add(new Stat("운동", "11시간"));
        items.add(new Stat("독서", "8시간"));

//        ㄴㄴ 리사이클
        statRecycler = statView.findViewById(R.id.statRecycler);
        statRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        statRecycler.setAdapter(new StatAdapter(statView.getContext(), items));
        statRecycler.setHasFixedSize(false);
        statRecycler.setNestedScrollingEnabled(false);

        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}