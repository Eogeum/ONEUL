package com.oneul.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oneul.extra.DateTime;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.dbHelper;

public class WriteFragment extends Fragment {

    //    뷰
    Button btnOk, timeStart, timeEnd;
    EditText editTitle, editMemo;

    //    디비
    dbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View writeView = inflater.inflate(R.layout.fragment_write, container, false);

//        뷰
        editTitle = writeView.findViewById(R.id.editTitle);
        editMemo = writeView.findViewById(R.id.editMemo);

//        시작 시간 입력
        timeStart = writeView.findViewById(R.id.timeStart);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeStart.setText(hourOfDay + " : " + minute);
                        timeStart.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, DateTime.nowHour(), DateTime.nowMinute(), true);

                timePickerDialog.setMessage("시작시간");
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
                        timeEnd.setText(hourOfDay + " : " + minute);
                        timeEnd.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, DateTime.nowHour(), DateTime.nowMinute(), true);

                timePickerDialog.setMessage("종료시간");
                timePickerDialog.show();

            }
        });

//        일과 저장
        btnOk = writeView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTitle.getText().toString())) {
                    Toast.makeText(getActivity(), "제목을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addOneul(MainActivity.showDay, timeStart.getText().toString(), timeEnd.getText().toString(),
                            editTitle.getText().toString(), editMemo.getText().toString(), 1);
//                    fixme : 널포인트 에러
                }
            }
        });


        return writeView;
    }

    public static WriteFragment newInstance(String param1, String param2) {
        WriteFragment fragment = new WriteFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WriteFragment() {
    }
}
