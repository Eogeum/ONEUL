package com.oneul.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.oneul.R;
import com.oneul.extra.StatAdapter;
import com.oneul.extra.StatItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatFragment extends Fragment {

    Button statLeft, statRight;
    TextView statYear, statMonth;
    PieChart pieChart;

    private RecyclerView statRecycler;
    private StatAdapter adapter;
    private RecyclerView.LayoutManager statLayoutManager;
    private ArrayList<StatItem> items;

    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        View statView = inflater.inflate(R.layout.fragment_stat, container, false);

        //년,월 표시
        statLeft = statView.findViewById(R.id.statLeft);
        statRight = statView.findViewById(R.id.statRight);
        statYear = statView.findViewById(R.id.statYear);
        statMonth = statView.findViewById(R.id.statMonth);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat(" MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("  yyyy", Locale.getDefault());

        String cuMonth = monthFormat.format(currentTime);
        String cuYear = yearFormat.format(currentTime);

        statYear.setText(cuYear);
        statMonth.setText(cuMonth);


        //파이차트
        pieChart = statView.findViewById(R.id.pieChart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> pValues = new ArrayList<PieEntry>();

        pValues.add(new PieEntry(1, "강의"));
        pValues.add(new PieEntry(2, "운동"));
        pValues.add(new PieEntry(3, "여가"));
        pValues.add(new PieEntry(4, "독서"));
        pValues.add(new PieEntry(5, "코딩"));


        //일과별 리스트
        items = new ArrayList<>();
        items.clear();
        items.add(new StatItem("코딩", "31시간"));
        items.add(new StatItem("강의", "24시간"));
        items.add(new StatItem("여가", "15시간"));
        items.add(new StatItem("운동", "11시간"));
        items.add(new StatItem("독서", "8시간"));

        Context context = statView.getContext();
        RecyclerView recyclerView = statView.findViewById(R.id.statRecycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        statRecycler = statView.findViewById(R.id.statRecycler);
        statRecycler.setHasFixedSize(true);
        statLayoutManager = new LinearLayoutManager(getActivity());
        statRecycler.setLayoutManager(statLayoutManager);
        statRecycler.scrollToPosition(0);
        adapter = new StatAdapter(context, items);
        statRecycler.setAdapter(adapter);
        statRecycler.setItemAnimator(new DefaultItemAnimator());

        return statView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

