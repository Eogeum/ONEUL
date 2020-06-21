package com.oneul.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.oneul.Animation;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.dbHelper;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {
    //    뷰
    Button btn_ok, btn_stop;
    EditText et_todayBox;
    LinearLayout ll_todayBox;
    ListView l_oneul;
    TextView t_oTitle, t_oTime, t_open, t_oMemo;
    ConstraintLayout cl_startBox;
    LinearLayout ll_memoBox;

    //    디비
    dbHelper dbHelper;
    int oNo;

    //    어댑터
    OneulAdapter adapter = new OneulAdapter(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        final View todayBox = getLayoutInflater().inflate(R.layout.home_todaybox, null, false);
        final View startBox = getLayoutInflater().inflate(R.layout.home_startbox, null, false);

//        뷰
        l_oneul = (ListView) homeView.findViewById(R.id.l_oneul);

        btn_ok = (Button) todayBox.findViewById(R.id.btn_ok);
        et_todayBox = (EditText) todayBox.findViewById(R.id.et_oTitle);
        ll_todayBox = (LinearLayout) todayBox.findViewById(R.id.ll_todayBox);

        t_oTitle = (TextView) startBox.findViewById(R.id.t_oTitle);
        t_oTime = (TextView) startBox.findViewById(R.id.t_oTime);
        t_oMemo = (TextView) startBox.findViewById(R.id.t_oMemo);
        t_open = (TextView) startBox.findViewById(R.id.t_open);
        cl_startBox = (ConstraintLayout) startBox.findViewById(R.id.cl_startBox);
        ll_memoBox = (LinearLayout) startBox.findViewById(R.id.ll_memoBox);
        btn_stop = (Button) startBox.findViewById(R.id.btn_stop);

//        디비
        dbHelper = new dbHelper(getActivity());

//       리스트
        TextView padding = new TextView(getActivity());
        padding.setPadding(0, 0, 0, 16);
        l_oneul.addHeaderView(todayBox);
        l_oneul.addFooterView(padding);
        l_oneul.setAdapter(null);

//        todo : 캘린더 뷰에서 오늘 날짜 값 받아오는 걸로 수정 지금 today = showDay 임
        final String showday = today();

//        시작 시 일과 불러오기
        dbHelper.getOneul(today(), l_oneul, adapter);

//        시작 시 기록중인 일과 확인
        if (dbHelper.getStartOneul() != null) {
//            기록중인 일과 불러오기
            startOneul(todayBox, startBox);
        }

//        오늘이 아닐 시 투데이박스 숨김
        if (!showday.equals(today())) {
            ll_todayBox.setVisibility(View.GONE);
        }

//        투데이박스 입력 완료 시
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                제목 널값 확인
                if (TextUtils.isEmpty(et_todayBox.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_todayBox.requestFocus();
                } else {
//                    키보드 내리기
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(homeView.getWindowToken(), 0);

//                    기록중인 일과 디비 저장
                    dbHelper.addOneul(today(), nowTime(), null, et_todayBox.getText().toString(),
                            null, 0);

//                    투데이박스 값 초기화
                    et_todayBox.getText().clear();

//                    기록중인 일과 디비에서 불러오기
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
                }
            }
        });

//        스타트 박스 입력 완료 시
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                일과 기록 종료, 로우 수정
                dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), nowTime(), t_oMemo.getText().toString());

//                일과 불러오기
                dbHelper.getOneul(showday, l_oneul, adapter);

//                리스트 헤더 투데이박스로 변경
                l_oneul.removeHeaderView(startBox);
                l_oneul.addHeaderView(todayBox);

                ll_memoBox.setVisibility(View.GONE);
            }
        });


        return homeView;
    }

//    오늘 날짜 불러오기
    private String today() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");

        return dateFormat.format(now);
    }

//    현재 시간 불러오기
    private String nowTime() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        return timeFormat.format(now);
    }

//    기록중인 일과 불러오기
    private void startOneul(View todayBox, View startBox) {
        //            리스트 헤더 스타트박스로 변경
        l_oneul.removeHeaderView(todayBox);
        l_oneul.addHeaderView(startBox);

        //            디비에서 기록중인 일과 불러오기
        Oneul startOneul = dbHelper.getStartOneul();
        oNo = startOneul.getoNo();
        t_oTime.setText(startOneul.getoStart());
        t_oTitle.setText(startOneul.getoTitle());
    }

    public HomeFragment() {
    }

    //    todo : 나중에 봐야할 코드 / 변수들 이름 수정
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}