package com.oneul.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.oneul.dbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    //    나중에 점검 해야할 코드들 ####################################
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //    XML 관련
    TextView t_oTitle, t_oTime;
    Button btn_todayBox;
    EditText et_todayBox;
    LinearLayout ll_todayBox;
    //    디비 관련
    dbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);

//        뷰 관련
        t_oTitle = homeView.findViewById(R.id.t_oTitle);
        t_oTime = homeView.findViewById(R.id.t_oTime);
        btn_todayBox = homeView.findViewById(R.id.btn_todayBox);
        et_todayBox = homeView.findViewById(R.id.et_todayBox);
        ll_todayBox = homeView.findViewById(R.id.ll_todayBox);

        //        디비 관련
        dbHelper = new dbHelper(getActivity());

//        날짜 관련
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-mm-dd");
        String showDay = dateFormat.format(now);
        String today = showDay;


//        오늘 날짜가 아닌 경우
        if (showDay != today) {
            ll_todayBox.setVisibility(View.GONE);
        }

        //        todayBox 입력 시
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

//                    일과 값 디비 저장
                    dbHelper.addOneul(et_todayBox.getText().toString(), startTime, startTime);

//                    일과 값 텍스트 뷰에 설정
                    String[] result = dbHelper.getOneul(1);
//
                    t_oTitle.setText(result[0]);
                    t_oTime.setText(result[1] + " ~ " + result[2]);
                }

            }
        });

        // Inflate the layout for this fragment
        return homeView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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


}