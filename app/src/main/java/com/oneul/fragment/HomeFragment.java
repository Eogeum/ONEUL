package com.oneul.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.oneul.dbHelper;
import com.oneul.oneul.OneulAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {

    //    뷰 관련
    Button btn_todayBox;
    EditText et_todayBox;
    LinearLayout ll_todayBox;
    ListView l_oneul;

    //    디비 관련
    dbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        인플레이터 관련
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        final View todayBox = getLayoutInflater().inflate(R.layout.home_oneul_todaybox, null, false);

//        어댑터 관련
        final OneulAdapter adapter = new OneulAdapter(this);

//        뷰 관련
        btn_todayBox = todayBox.findViewById(R.id.btn_todayBox);
        et_todayBox = todayBox.findViewById(R.id.et_todayBox);
        ll_todayBox = todayBox.findViewById(R.id.ll_todayBox);
        l_oneul = homeView.findViewById(R.id.l_oneul);
        l_oneul.addHeaderView(todayBox);
        l_oneul.setAdapter(null);

//        디비 관련
        dbHelper = new dbHelper(getActivity());

//        날짜 관련
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        final String showDay = dateFormat.format(now);
        String today = showDay;


//        todo : 캘린더 뷰에서 오늘 날짜 값 받아오는 걸로 수정 지금 today = showDay 임
//        시작 시 일과 불러오기
        dbHelper.getOneul(showDay, l_oneul, adapter);

//        오늘이 아닐 시 투데이박스 숨김
        if (showDay != today) {
            ll_todayBox.setVisibility(View.GONE);
        }

//        투데이박스 입력 시
        btn_todayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                제목 널값 확인
                if (TextUtils.isEmpty(et_todayBox.getText().toString())) {
                    Toast.makeText(getActivity().getApplicationContext(), "일과 제목을 입력해주세요.",
                            Toast.LENGTH_SHORT).show();
                    et_todayBox.requestFocus();

                } else {

//                    시간 관련
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


    //    나중에 점검 해야할 코드들 ####################################

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */

    // TODO: Rename and change types and number of parameters
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