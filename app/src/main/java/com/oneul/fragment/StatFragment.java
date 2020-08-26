package com.oneul.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.oneul.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatFragment extends Fragment {

    Button statLeft, statRight;
    TextView statYear, statMonth;
    PieChart pieChart;

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
        statYear = statView.findViewById(R.id.statYear);
        statMonth = statView.findViewById(R.id.statMonth);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat(" MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("  yyyy", Locale.getDefault());

        String cuMonth = monthFormat.format(currentTime);
        String cuYear = yearFormat.format(currentTime);

        statYear.setText(cuYear);
        statMonth.setText(cuMonth);

        pieChart = statView.findViewById(R.id.pieChart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> pValues = new ArrayList<PieEntry>();

        pValues.add(new PieEntry(1, "공부"));
        pValues.add(new PieEntry(2, "운동"));
        pValues.add(new PieEntry(3, "친구"));
        pValues.add(new PieEntry(4, "TV"));
        pValues.add(new PieEntry(5, "과제"));



        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}