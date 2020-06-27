package com.oneul.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.Animation;
import com.oneul.extra.DateTime;
import com.oneul.extra.dbHelper;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;

public class HomeFragment extends Fragment {
    //    뷰
    Button btn_ok, btn_stop;
    public static EditText et_todayBox;
    LinearLayout ll_todayBox;
    ListView l_oneul;
    TextView t_oTitle, t_oTime, t_open, t_oMemo;
    ConstraintLayout cl_startBox;
    LinearLayout ll_memoBox;
    CalendarView c_cal;

    //    디비
    dbHelper dbHelper;
    int oNo;

    //    어댑터
    OneulAdapter adapter = new OneulAdapter(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        final View todayBox = inflater.inflate(R.layout.home_todaybox, null, false);
        final View startBox = inflater.inflate(R.layout.home_startbox, null, false);

        //       뷰
        l_oneul = homeView.findViewById(R.id.l_oneul);
        c_cal = homeView.findViewById(R.id.c_cal);

        btn_ok = todayBox.findViewById(R.id.btn_ok);
        et_todayBox = todayBox.findViewById(R.id.et_oTitle);
        ll_todayBox = todayBox.findViewById(R.id.ll_todayBox);

        t_oTitle = startBox.findViewById(R.id.t_oTitle);
        t_oTime = startBox.findViewById(R.id.t_oTime);
        t_oMemo = startBox.findViewById(R.id.t_oMemo);
        t_open = startBox.findViewById(R.id.t_open);
        cl_startBox = startBox.findViewById(R.id.cl_startBox);
        ll_memoBox = startBox.findViewById(R.id.ll_memoBox);
        btn_stop = startBox.findViewById(R.id.btn_stop);

//        디비
        dbHelper = new dbHelper(getActivity());

//        리스트
        TextView padding = new TextView(getActivity());
        padding.setPadding(0, 0, 0, 120);
        l_oneul.addFooterView(padding);
        l_oneul.setAdapter(null);

//        시작 시 날짜
        boxChange(todayBox, startBox);

//        시작 시 일과 불러오기
        dbHelper.getOneul(MainActivity.showDay, l_oneul, adapter);

//        데이터 불러오기
        et_todayBox.setText(MainActivity.inputText);

//        캘린더 클릭 시
        c_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String pickYear = Integer.toString(year).substring(2);
                String pickMonth = Integer.toString(month + 1);
                String pickDay = Integer.toString(dayOfMonth);

                MainActivity.showDay = pickYear + "/" + pickMonth + "/" + pickDay;

//                일과 불러오기
                dbHelper.getOneul(MainActivity.showDay, l_oneul, adapter);
                boxChange(todayBox, startBox);
            }
        });

//        투데이박스 입력 완료 시
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                제목 널값 확인
                if (TextUtils.isEmpty(et_todayBox.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();

//                    투데이박스 포커스, 키보드 올리기
                    et_todayBox.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_todayBox, InputMethodManager.SHOW_IMPLICIT);
                } else {
//                    키보드 내리기
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(homeView.getWindowToken(), 0);

//                    기록중인 일과 디비 저장
                    dbHelper.addOneul(DateTime.today(), DateTime.nowTime(), null, et_todayBox.getText().toString(), null, 0);

//                    투데이박스 값 초기화
                    et_todayBox.getText().clear();

//                    기록중인 일과 불러오기
                    startOneul(todayBox, startBox);
                }
            }
        });

//        메모박스 확장 축소
        cl_startBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ll_memoBox.getVisibility()) {
                    case View.GONE:
                        Animation.expand(ll_memoBox);
                        t_open.setText("∧");
                        break;

                    case View.VISIBLE:
                        Animation.collapse(ll_memoBox);
                        t_open.setText("∨");
                        break;

                    case View.INVISIBLE:
                        Toast.makeText(getActivity(), "ERROR : RETURN INVISIBLE", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

//        스타트 박스 기록 완료 시
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                일과 기록 종료, 로우 수정
                dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), DateTime.nowTime(), t_oMemo.getText().toString());

//                일과 불러오기
                dbHelper.getOneul(DateTime.today(), l_oneul, adapter);

//                리스트 헤더 투데이박스로 변경
                l_oneul.removeHeaderView(startBox);
                l_oneul.addHeaderView(todayBox);

                ll_memoBox.setVisibility(View.GONE);
            }
        });

        return homeView;
    }

    //    날짜
    private void boxChange(View todayBox, View startBox) {
        if (!TextUtils.equals(MainActivity.showDay, DateTime.today())) {
            l_oneul.removeHeaderView(todayBox);
            l_oneul.removeHeaderView(startBox);
        } else {
            if (dbHelper.getStartOneul() != null) {
                startOneul(todayBox, startBox);
            } else {
                l_oneul.addHeaderView(todayBox);
            }
        }
    }

    //    기록중인 일과 불러오기
    private void startOneul(View todayBox, View startBox) {
//        리스트 헤더 스타트박스로 변경
        l_oneul.removeHeaderView(todayBox);
        l_oneul.addHeaderView(startBox);

//        디비에서 기록중인 일과 불러오기
        Oneul startOneul = dbHelper.getStartOneul();
        oNo = startOneul.getoNo();
        t_oTime.setText(startOneul.getoStart());
        t_oTitle.setText(startOneul.getoTitle());
    }

    //    화면 전환
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public HomeFragment() {
    }
}