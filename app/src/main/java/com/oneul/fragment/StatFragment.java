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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.oneul.R;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.stat.StatAdapter;

import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class StatFragment extends Fragment {
    //    ㄴㄴ 데이터
    String showDay = DateTime.today();
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월");

    //    ㄴㄴ 리사이클
    public static PieChart pieChart;
    public static RecyclerView statRecycler;
    public static StatAdapter adapter = new StatAdapter();

    //    ㄴㄴ 뷰
    TextView statDay;
    SwipeRefreshLayout sr_swipeRefresh;

    //    ㄴㄴ 기타
    DBHelper dbHelper;

    SwipeRefreshLayout.OnRefreshListener listener;

    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //ㄴㄴ 인플레이터
        View statView = inflater.inflate(R.layout.fragment_stat, container, false);

//        ㄴㄴ 리사이클
        pieChart = statView.findViewById(R.id.pieChart);
        pieChart.setDescription(null);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);

        statRecycler = statView.findViewById(R.id.statRecycler);
        statRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        statRecycler.setAdapter(adapter);
        statRecycler.setHasFixedSize(false);
        statRecycler.setNestedScrollingEnabled(false);

        // ㄴㄴ 뷰
        statDay = statView.findViewById(R.id.statDay);

        //    ㄴㄴ 기타
        dbHelper = DBHelper.getDB(getContext());

        listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    statDay.setText(format2.format(Objects.requireNonNull(format1.parse(showDay))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dbHelper.getStat(showDay);
                pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
                sr_swipeRefresh.setRefreshing(false);
            }
        };
        sr_swipeRefresh = statView.findViewById(R.id.sr_swipeRefresh);
        sr_swipeRefresh.setColorSchemeResources(R.color.mainColor);
        sr_swipeRefresh.setOnRefreshListener(listener);
        listener.onRefresh();

        statView.findViewById(R.id.i_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDay = LocalDate.parse(showDay).minusMonths(1).toString();
                listener.onRefresh();
            }
        });

        statView.findViewById(R.id.i_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDay = LocalDate.parse(showDay).plusMonths(1).toString();
                listener.onRefresh();
            }
        });

        return statView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}