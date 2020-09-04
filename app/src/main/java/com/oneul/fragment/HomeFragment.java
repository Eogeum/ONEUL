package com.oneul.fragment;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.oneul.WriteActivity;
import com.oneul.calendar.OneulDecorator;
import com.oneul.extra.Animation;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Objects;

public class HomeFragment extends Fragment {
    //    ㄴㄴ 뷰
    LinearLayout ll_todayBox, ll_goCalendar, ll_picMemo, ll_cancelMemo, ll_saveMemo;
    Button btn_ok, btn_stop;
    EditText et_oTitle, et_oMemo;
    FrameLayout fl_startBox;
    ConstraintLayout cl_startBox;
    TextView t_oTitle, t_oTime, t_oNo, t_oDate;
    ImageView i_memoBox;
    LinearLayout ll_memoBox;

    //    ㄴㄴ 키보드
    InputMethodManager imm;

    //    ㄴㄴ 디비
    public static DBHelper dbHelper;
    public static OneulAdapter adapter = new OneulAdapter();

    //    ㄴㄴ 리사이클
    public static RecyclerView r_oneul;

    //    ㄴㄴ fab
    FloatingActionButton fab_goWrite;

    //    ㄴㄴ 캘린더
    View calendarView;
    MaterialCalendarView widget;
    AlertDialog calendarDialog;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ㄴㄴ 인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);

//        ㄴㄴ 뷰
        t_oDate = homeView.findViewById(R.id.t_oDate);
//        투데이박스
        ll_todayBox = homeView.findViewById(R.id.ll_todayBox);
        et_oTitle = homeView.findViewById(R.id.et_oTitle);
//        스타트박스
        fl_startBox = homeView.findViewById(R.id.fl_startBox);
        i_memoBox = homeView.findViewById(R.id.i_memoBox);
        t_oTitle = homeView.findViewById(R.id.t_oTitle);
        t_oTime = homeView.findViewById(R.id.t_oTime);
//        스타트박스 메모
        ll_memoBox = homeView.findViewById(R.id.ll_memoBox);

//        ㄴㄴ 키보드
        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

//        ㄴㄴ 디비
        dbHelper = new DBHelper(getActivity());

//        ㄴㄴ 리사이클
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        r_oneul = homeView.findViewById(R.id.r_oneul);
        r_oneul.setLayoutManager(linearLayoutManager);
        r_oneul.setHasFixedSize(true);
        r_oneul.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    fab_goWrite.show();
                } else if (dy > 0) {
                    fab_goWrite.hide();
                }
            }
        });

        //        ㄴㄴ fab
        fab_goWrite = homeView.findViewById(R.id.fab_goWrite);
        fab_goWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WriteActivity.class));
            }
        });

        //        ㄴㄴ 캘린더
        calendarView = inflater.inflate(R.layout.fragment_calendar, null);
        widget = calendarView.findViewById(R.id.mc_calendar);
        //        최소 최대 날짜 설정
        widget.state().edit()
                .setMinimumDate(CalendarDay.from(2000, 1, 1))
                .setMaximumDate(CalendarDay.from(2040, 1, 1))
                .commit();
        widget.setSelectedDate(LocalDate.parse(MainActivity.showDay));
//        캘린더 헤더 수정
        widget.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getDate().format(DateTimeFormatter.ofPattern("yyyy. MM"));
            }
        });
        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                MainActivity.showDay = widget.getSelectedDate().getDate().toString();
                dateChange();
                calendarDialog.dismiss();
            }
        });
//        캘린더 데코레이터
        widget.addDecorators(new OneulDecorator(dbHelper.getOneulDates()));

//        캘린더 열기
        ll_goCalendar = homeView.findViewById(R.id.ll_goCalendar);
        ll_goCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                다이얼로그가 널이면
                if (calendarDialog == null) {
                    calendarDialog = DialogFragment.calendarDialog(getActivity(), calendarView);
                }

                widget.setSelectedDate(LocalDate.parse(MainActivity.showDay));
                calendarDialog.show();
            }
        });

//        투데이박스 입력 완료
        btn_ok = homeView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                일과 제목이 없으면
                if (TextUtils.isEmpty(et_oTitle.getText().toString())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();

//                    투데이박스 포커스, 키보드 올리기
                    et_oTitle.requestFocus();
                    imm.showSoftInput(et_oTitle, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (dbHelper.getStartOneul() == null) {
//                        기록 시작
                        dbHelper.addOneul(DateTime.today(), DateTime.nowTime(), null, et_oTitle.getText().toString(),
                                null, 0);
                    }

//                    새로고침 및 투데이박스 값 초기화
                    dateChange();
                    et_oTitle.getText().clear();
                }
            }
        });

//        메모박스 확장 축소
        cl_startBox = homeView.findViewById(R.id.cl_startBox);
        cl_startBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ll_memoBox.getVisibility()) {
                    case View.GONE:
                        Animation.expand(ll_memoBox);
                        i_memoBox.setImageResource(R.drawable.ic_callapse);
                        break;

                    case View.VISIBLE:
                        if (MainActivity.useEditMemo) {
                            DialogFragment.editMemoDialog(getActivity(), 0);
                        } else {
                            Animation.collapse(ll_memoBox);
                            i_memoBox.setImageResource(R.drawable.ic_expand);
                        }
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

//        사진메모 작성 활성화
        ll_picMemo = homeView.findViewById(R.id.ll_pictureMemo);
        ll_picMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment.UploadImageDialog(getActivity());
            }
        });

//        메모 작성 활성화
        et_oMemo = homeView.findViewById(R.id.et_oMemo);
        et_oMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                에딧 텍스트 포커싱 시
                if (hasFocus) {
                    imm.showSoftInput(et_oMemo, InputMethodManager.SHOW_IMPLICIT);
                    ll_picMemo.setVisibility(View.GONE);
                    ll_cancelMemo.setVisibility(View.VISIBLE);
                    ll_saveMemo.setVisibility(View.VISIBLE);
                    MainActivity.useEditMemo = true;
                }
            }
        });

//        메모 작성 취소
        ll_cancelMemo = homeView.findViewById(R.id.ll_cancelMemo);
        ll_cancelMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_picMemo.setVisibility(View.VISIBLE);
                ll_cancelMemo.setVisibility(View.GONE);
                ll_saveMemo.setVisibility(View.GONE);
                MainActivity.useEditMemo = false;

//                새로고침
                et_oMemo.setText(dbHelper.getStartOneul().getoMemo());
            }
        });

//        작성 메모 저장
        ll_saveMemo = homeView.findViewById(R.id.ll_saveMemo);
        ll_saveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_picMemo.setVisibility(View.VISIBLE);
                ll_cancelMemo.setVisibility(View.GONE);
                ll_saveMemo.setVisibility(View.GONE);
                MainActivity.useEditMemo = false;

//                메모 저장 및 새로고침
                dbHelper.editMemo(dbHelper.getStartOneul().getoNo(), et_oMemo.getText().toString());
                Toast.makeText(getActivity(), "메모를 저장했습니다.", Toast.LENGTH_LONG).show();
            }
        });

//        스타트박스 기록 완료
        btn_stop = homeView.findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                메모 작성중일 시
                if (MainActivity.useEditMemo) {
                    DialogFragment.editMemoDialog(getActivity(), 0);
                } else {
//                    쇼데이 변경
                    MainActivity.showDay = dbHelper.getStartOneul().getoDate();

//                    기록 종료 및 새로고침
                    dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), DateTime.nowTime());
                    Toast.makeText(getActivity(), MainActivity.showDay + "\n일과를 저장했습니다.",
                            Toast.LENGTH_LONG).show();
                    dateChange();

//                    메모박스 초기화
                    i_memoBox.setImageResource(R.drawable.ic_expand);
                    Animation.collapse(ll_memoBox);
                }
            }
        });

        //        시작 시
        dateChange();

        return homeView;
    }

    //    날짜 확인 및 헤더 변경
    public void dateChange() {
//        오늘이면
        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
//            기록중인 일과 있으면
            if (dbHelper.getStartOneul() != null) {
//                스타트박스 표시
                ll_todayBox.setVisibility(View.GONE);
                fl_startBox.setVisibility(View.VISIBLE);

//                디비에서 기록중인 일과 불러오기
                Oneul startOneul = dbHelper.getStartOneul();
                t_oTime.setText(startOneul.getoStart());
                t_oTitle.setText(startOneul.getoTitle());
                et_oMemo.setText(startOneul.getoMemo());
            } else {
//                투데이박스 표시
                ll_todayBox.setVisibility(View.VISIBLE);
                fl_startBox.setVisibility(View.GONE);
            }
        } else {
            ll_todayBox.setVisibility(View.GONE);
            fl_startBox.setVisibility(View.GONE);
        }

//        일과 불러오기
        t_oDate.setText(MainActivity.showDay);
        dbHelper.getOneul(MainActivity.showDay, r_oneul, adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        dateChange();
    }
}