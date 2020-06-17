package com.oneul.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.oneul.dbHelper;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class WriteFragment extends Fragment {

//    XML 위젯
    int h = 0, mi = 0;
    Button btnOk, timeStart, timeEnd;
    String tStart, tEnd;
    EditText editTitle, editMemo;

    dbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View writeView = inflater.inflate(R.layout.fragment_write, container, false);

//        시작 시간 입력
        timeStart = writeView.findViewById(R.id.timeStart);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        h = hourOfDay;
                        mi = minute;

                        tStart= hourOfDay + " : " + minute;
                        timeStart.setText(tStart);
                    }
                }, 21, 12, true);

                timePickerDialog.setMessage("일과 시작");
                timePickerDialog.show();

            }
        });

//        종료 시간 입력
        timeEnd = writeView.findViewById(R.id.timeEnd);
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        h = hourOfDay;
                        mi = minute;

                        tEnd = hourOfDay + " : " + minute;
                        timeEnd.setText(tEnd);
                    }
                }, 21, 12, true);

                timePickerDialog.setMessage("일과 종료");
                timePickerDialog.show();

            }
        });

//        일과 저장
        btnOk = writeView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.addOneul(editTitle.getText().toString(), tStart, tEnd);
            }
        });


        // Inflate the layout for this fragment
        return writeView;
    }



    //    나중에 점검 해야할 코드들 ####################################

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteFragment newInstance(String param1, String param2) {
        WriteFragment fragment = new WriteFragment();
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
