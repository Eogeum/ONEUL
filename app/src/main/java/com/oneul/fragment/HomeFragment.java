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

import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.oneul.dbHelper;
import com.oneul.oneul.OneulAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

//    뷰
    Button btn_todayBox;
    EditText et_todayBox;
    LinearLayout ll_todayBox;
    ListView l_oneul;

//    디비
    dbHelper dbHelper;

//    어댑터
    OneulAdapter adapter = new OneulAdapter(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        인플레이터
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        View todayBox = getLayoutInflater().inflate(R.layout.home_oneul_todaybox, null, false);

//        뷰
        l_oneul = homeView.findViewById(R.id.l_oneul);
        btn_todayBox = todayBox.findViewById(R.id.btn_todayBox);
        et_todayBox = todayBox.findViewById(R.id.et_todayBox);
        ll_todayBox = todayBox.findViewById(R.id.ll_todayBox);

//        디비
        dbHelper = new dbHelper(getActivity());;

//       리스트
        TextView padding = new TextView(getActivity());
        padding.setPadding(0, 0, 0, 16);
        l_oneul.addHeaderView(todayBox);
        l_oneul.addFooterView(padding);
        l_oneul.setAdapter(null);

//        날짜
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        final String showDay = dateFormat.format(now);
        String today = showDay;


//        todo : 캘린더 뷰에서 오늘 날짜 값 받아오는 걸로 수정 지금 today = showDay 임
//        시작 시 일과 불러오기
        dbHelper.getOneul(showDay, l_oneul, adapter);

//        오늘이 아닐 시 투데이박스 숨김
        if (!showDay.equals(today)) {
            ll_todayBox.setVisibility(View.GONE);
        }

//        투데이박스 입력 시
        btn_todayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                제목 널값 확인
                if (TextUtils.isEmpty(et_todayBox.getText().toString())) {
                    Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_todayBox.requestFocus();

                } else {

//                    키보드 내리기
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(homeView.getWindowToken(), 0);

//                    시간
                    Date now = new Date(System.currentTimeMillis());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                    String startTime = timeFormat.format(now);

//                    todo : 엔드 버튼이 생기면 엔드타임도 설정해서 넣어줘야 함 지금 endTime = startTime 임
//                    입력한 일과 디비에 저장
                    dbHelper.addOneul(showDay, startTime, startTime, et_todayBox.getText().toString());

//                    일과 불러오기
                    dbHelper.getOneul(showDay, l_oneul, adapter);

                }
            }
        });



        return homeView;
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