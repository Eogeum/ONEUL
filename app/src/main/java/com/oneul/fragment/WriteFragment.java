package com.oneul.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.DateTime;
import com.oneul.extra.dbHelper;

import java.util.Objects;

public class WriteFragment extends Fragment {
    //    뷰
    Button btnOk, timeStart, timeEnd;
    EditText editTitle, editMemo;

    //    디비
    dbHelper dbHelper;

    public WriteFragment() {
    }

    public static WriteFragment newInstance() {
        return new WriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        인플레이터
        final View writeView = inflater.inflate(R.layout.fragment_write, container, false);

//        뷰
        editTitle = writeView.findViewById(R.id.editTitle);
        editMemo = writeView.findViewById(R.id.editMemo);

//        디비
        dbHelper = new dbHelper(getActivity());

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
                    Toast.makeText(getActivity(), "일과 제목을 입력하세요.", Toast.LENGTH_SHORT).show();

//                    에딧 텍스트 포커스, 키보드 올리기
                    editTitle.requestFocus();
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    dbHelper.addOneul(MainActivity.showDay, timeStart.getText().toString(), timeEnd.getText().toString(),
                            editTitle.getText().toString(), editMemo.getText().toString(), 1);

                    Toast.makeText(getActivity(), "일과가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    MainActivity.bot_menu.setSelectedItemId(R.id.bot_menu_home);
                }
            }
        });

        return writeView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    todo: 홈화면 추가 버튼으로 이동 버튼 변경
//    todo: 닫는 버튼이 필요해지고 뒤로가기로 다이얼로그 (작성 취소하겠습니까?)등 표시
}