package com.oneul.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class HomeFragment extends Fragment {
    //    뷰
    public static EditText et_todayBox, et_oMemo;
    Button btn_ok, btn_stop, btn_picMemo, btn_cancelMemo, btn_saveMemo;
    LinearLayout ll_todayBox;
    ListView l_oneul;
    TextView t_oTitle, t_oTime, t_open, t_oNo;
    ConstraintLayout cl_startBox;
    LinearLayout ll_memoBox;
    CalendarView c_cal;

    //    키보드
    InputMethodManager imm;

    //    디비
    dbHelper dbHelper;

    //    어댑터
    OneulAdapter adapter = new OneulAdapter(this);

    public HomeFragment() {
    }

    //    화면 전환
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        final View todayBox = inflater.inflate(R.layout.home_todaybox, l_oneul, false);
        final View startBox = inflater.inflate(R.layout.home_startbox, l_oneul, false);

//       뷰
        l_oneul = homeView.findViewById(R.id.l_oneul);
        c_cal = homeView.findViewById(R.id.c_cal);

        btn_ok = todayBox.findViewById(R.id.btn_ok);
        et_todayBox = todayBox.findViewById(R.id.et_oTitle);
        ll_todayBox = todayBox.findViewById(R.id.ll_todayBox);

        t_oTitle = startBox.findViewById(R.id.t_oTitle);
        t_oTime = startBox.findViewById(R.id.t_oTime);
        et_oMemo = startBox.findViewById(R.id.et_oMemo);
        t_open = startBox.findViewById(R.id.t_open);
        t_oNo = startBox.findViewById(R.id.t_oNo);
        cl_startBox = startBox.findViewById(R.id.cl_startBox);
        ll_memoBox = startBox.findViewById(R.id.ll_memoBox);
        btn_stop = startBox.findViewById(R.id.btn_stop);
        btn_picMemo = startBox.findViewById(R.id.btn_picMemo);
        btn_cancelMemo = startBox.findViewById(R.id.btn_cancelMemo);
        btn_saveMemo = startBox.findViewById(R.id.btn_saveMemo);

//        키보드
        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

//        디비
        dbHelper = new dbHelper(getActivity());

//        리스트
        TextView padding = new TextView(getActivity());
        padding.setPadding(0, 0, 0, 120);
        l_oneul.addFooterView(padding);
        l_oneul.setAdapter(null);

//        시작 시
        t_oTitle.setSelected(true);
        dateChange(todayBox, startBox);

        try {
            c_cal.setDate(Objects.requireNonNull(new SimpleDateFormat("yy/M/d").parse(MainActivity.showDay)).getTime(), false, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        입력하던 투데이 박스 값 불러오기
        et_todayBox.setText(MainActivity.inputText);

//        날짜 변경
        c_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String pickYear = Integer.toString(year).substring(2);
                String pickMonth = Integer.toString(month + 1);
                String pickDay = Integer.toString(dayOfMonth);

                MainActivity.showDay = pickYear + "/" + pickMonth + "/" + pickDay;
                dateChange(todayBox, startBox);
    }
});

//        투데이박스 입력 완료
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                일과 제목이 없으면
                if (TextUtils.isEmpty(et_todayBox.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();

//                    투데이박스 포커스, 키보드 올리기
                    et_todayBox.requestFocus();
                    imm.showSoftInput(et_todayBox, InputMethodManager.SHOW_IMPLICIT);
                } else {
//                    기록 시작 및 새로고침
                    dbHelper.addOneul(DateTime.today(), DateTime.nowTime(), null, et_todayBox.getText().toString(), null, 0);
                    dateChange(todayBox, startBox);

//                    투데이박스 값 초기화
                    et_todayBox.getText().clear();
                }
            }
        });

//        메모박스 확장 축소
        cl_startBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ll_memoBox.getVisibility()) {
                    case View.GONE:
                        t_open.setText("∧");
                        Animation.expand(ll_memoBox);
                        break;

                    case View.VISIBLE:
                        t_open.setText("∨");
                        Animation.collapse(ll_memoBox);
                        break;

                    case View.INVISIBLE:
                        Toast.makeText(getActivity(), "ERROR : RETURN INVISIBLE", Toast.LENGTH_LONG).show();
                        break;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = ll_memoBox.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        ll_memoBox.setLayoutParams(layoutParams);
                    }
                }, 1000);
            }
        });

//        메모 작성 활성화
        et_oMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                에딧 텍스트 포커싱 되면
                if (hasFocus) {
                    btn_picMemo.setVisibility(View.GONE);
                    imm.showSoftInput(et_oMemo, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    btn_cancelMemo.setVisibility(View.VISIBLE);
                    btn_saveMemo.setVisibility(View.VISIBLE);
                }
            }
        });

//        메모 작성 취소
        btn_cancelMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_picMemo.setVisibility(View.VISIBLE);
                btn_cancelMemo.setVisibility(View.GONE);
                btn_saveMemo.setVisibility(View.GONE);

//                새로고침
                dateChange(todayBox, startBox);
            }
        });

//        작성 메모 저장
        btn_saveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_picMemo.setVisibility(View.VISIBLE);
                btn_cancelMemo.setVisibility(View.GONE);
                btn_saveMemo.setVisibility(View.GONE);

//                메모 저장 및 새로고침
                dbHelper.editMemo(dbHelper.getStartOneul().getoNo(), et_oMemo.getText().toString());
                dateChange(todayBox, startBox);

                Toast.makeText(getActivity(), "메모를 저장했습니다.", Toast.LENGTH_LONG).show();
            }
        });

//        스타트박스 기록 완료
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                기록 종료 및 새로고침
                dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), DateTime.nowTime());
                dateChange(todayBox, startBox);

//                메모박스 축소
                t_open.setText("∨");
                Animation.collapse(ll_memoBox);
            }
        });

        return homeView;
    }

    //    날짜 확인 및 헤더 변경
    private void dateChange(View todayBox, View startBox) {
        l_oneul.removeHeaderView(todayBox);
        l_oneul.removeHeaderView(startBox);

//        오늘이면
        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
//            기록중인 일과 있으면
            if (dbHelper.getStartOneul() != null) {
//                리스트 헤더 스타트박스로 변경
                l_oneul.removeHeaderView(todayBox);
                l_oneul.addHeaderView(startBox);

//                디비에서 기록중인 일과 불러오기
                Oneul startOneul = dbHelper.getStartOneul();
                t_oTime.setText(startOneul.getoStart());
                t_oTitle.setText(startOneul.getoTitle());
                et_oMemo.setText(startOneul.getoMemo());
            } else {
//                리스트 헤더 투데이박스로 변경
                l_oneul.addHeaderView(todayBox);
            }
        }

//        일과 불러오기
        dbHelper.getOneul(MainActivity.showDay, l_oneul, adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    todo: 화면 전환, 날짜 변경 시 투데이박스 값, 스타트박스 메모 값 등 유지 계쏙 할건지
//    todo: 캘린더 클릭 시 뉴인스턴스로 해결가능
//    todo: 슬라이딩 드로우 다른 걸로 교체
//    todo: 화면 전환 시 새로운 프래그먼트로 불러오지 말고 기존 프래그먼트로 불러오게
}