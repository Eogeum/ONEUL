package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.WhiteActivity;
import com.oneul.extra.Animation;
import com.oneul.extra.DateTime;
import com.oneul.extra.OnSingleClickListener;
import com.oneul.extra.DBHelper;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class HomeFragment extends Fragment {
    //    뷰
    RecyclerView r_oneul;
    CalendarView c_cal;
    FloatingActionButton fab_goWhite;

    LinearLayout ll_todayBox;
    Button btn_ok, btn_stop, btn_picMemo, btn_cancelMemo, btn_saveMemo;
    EditText et_oTitle, et_oMemo;

    FrameLayout fl_startBox;
    ConstraintLayout cl_startBox;
    TextView t_oTitle, t_oTime, t_open, t_oNo;
    LinearLayout ll_memoBox;

    //    키보드
    InputMethodManager imm;

    //    디비
    DBHelper dbHelper;

    //    어댑터
    OneulAdapter adapter = new OneulAdapter();

    public HomeFragment() {
    }

    //    화면 전환
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);

//       뷰
        r_oneul = homeView.findViewById(R.id.r_oneul);
        c_cal = homeView.findViewById(R.id.c_cal);
        fab_goWhite = homeView.findViewById(R.id.fab_goWrite);

//        투데이박스
        ll_todayBox = homeView.findViewById(R.id.ll_todayBox);
        btn_ok = homeView.findViewById(R.id.btn_ok);
        et_oTitle = homeView.findViewById(R.id.et_oTitle);

//        스타트박스
        fl_startBox = homeView.findViewById(R.id.fl_startBox);
        cl_startBox = homeView.findViewById(R.id.cl_startBox);
        btn_stop = homeView.findViewById(R.id.btn_stop);
        t_open = homeView.findViewById(R.id.t_open);
        t_oTitle = homeView.findViewById(R.id.t_oTitle);
        t_oTime = homeView.findViewById(R.id.t_oTime);

//        스타트박스 메모
        ll_memoBox = homeView.findViewById(R.id.ll_memoBox);
        btn_picMemo = homeView.findViewById(R.id.btn_picMemo);
        btn_cancelMemo = homeView.findViewById(R.id.btn_cancelMemo);
        btn_saveMemo = homeView.findViewById(R.id.btn_saveMemo);
        et_oMemo = homeView.findViewById(R.id.et_oMemo);

//        키보드
        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

//        디비
        dbHelper = new DBHelper(getActivity());

//        리스트
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        r_oneul.setLayoutManager(linearLayoutManager);
        r_oneul.setHasFixedSize(true);

//        시작 시
        dateChange();

        try {
            c_cal.setDate(Objects.requireNonNull(new SimpleDateFormat("yyyy/MM/dd").parse(MainActivity.showDay)).getTime(), false, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        입력하던 투데이 박스 값 불러오기
        et_oTitle.setText(MainActivity.inputText);

//        날짜 변경
        c_cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                MainActivity.showDay = String.format(year + "/%02d/%02d", month + 1, dayOfMonth);
                dateChange();
            }
        });

//        투데이박스 입력 완료
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                일과 제목이 없으면
                if (TextUtils.isEmpty(et_oTitle.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();

//                    투데이박스 포커스, 키보드 올리기
                    et_oTitle.requestFocus();
                    imm.showSoftInput(et_oTitle, InputMethodManager.SHOW_IMPLICIT);
                } else {
//                    기록 시작 및 새로고침
                    dbHelper.addOneul(DateTime.today(), DateTime.nowTime(), null, et_oTitle.getText().toString(), null, 0);
                    dateChange();

//                    투데이박스 값 초기화
                    et_oTitle.getText().clear();
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
                    btn_cancelMemo.setVisibility(View.VISIBLE);
                    btn_saveMemo.setVisibility(View.VISIBLE);
                    MainActivity.useEditMemo = true;
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
                MainActivity.useEditMemo = false;

//                새로고침
                dateChange();
            }
        });

//        작성 메모 저장
        btn_saveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_picMemo.setVisibility(View.VISIBLE);
                btn_cancelMemo.setVisibility(View.GONE);
                btn_saveMemo.setVisibility(View.GONE);
                MainActivity.useEditMemo = false;

//                메모 저장 및 새로고침
                dbHelper.editMemo(dbHelper.getStartOneul().getoNo(), et_oMemo.getText().toString());
                dateChange();

                Toast.makeText(getActivity(), "메모를 저장했습니다.", Toast.LENGTH_LONG).show();
            }
        });

//        스타트박스 기록 완료
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                쇼데이 변경
                MainActivity.showDay = dbHelper.getStartOneul().getoDate();

//                기록 종료 및 새로고침
                dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), DateTime.nowTime());
                Toast.makeText(getActivity(), MainActivity.showDay + "\n일과를 저장했습니다.", Toast.LENGTH_LONG).show();
                dateChange();

//                메모박스 축소
                t_open.setText("∨");
                Animation.collapse(ll_memoBox);
            }
        });

//        fab 클릭 시
        fab_goWhite.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), WhiteActivity.class);
                startActivity(intent);
            }
        });

        return homeView;
    }

    //    날짜 확인 및 헤더 변경
    private void dateChange() {
        ll_todayBox.setVisibility(View.GONE);
        fl_startBox.setVisibility(View.GONE);

//        오늘이면
        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
//            기록중인 일과 있으면
            if (dbHelper.getStartOneul() != null) {
//                스타트박스 표시
                fl_startBox.setVisibility(View.VISIBLE);

//                디비에서 기록중인 일과 불러오기
                Oneul startOneul = dbHelper.getStartOneul();
                t_oTime.setText(startOneul.getoStart());
                t_oTitle.setText(startOneul.getoTitle());
                et_oMemo.setText(startOneul.getoMemo());
            } else {
//                투데이박스 표시
                ll_todayBox.setVisibility(View.VISIBLE);
            }
        }

//        일과 불러오기
        dbHelper.getOneul(MainActivity.showDay, r_oneul, adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MainActivity.inputText = et_oTitle.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        dbHelper.getOneul(MainActivity.showDay, r_oneul, adapter);
    }
}