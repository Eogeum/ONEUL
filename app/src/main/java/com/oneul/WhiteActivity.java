package com.oneul;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oneul.extra.DateTime;
import com.oneul.extra.DBHelper;

import java.util.Objects;

public class WhiteActivity extends AppCompatActivity {

    //    뷰
    Button btnOk, timeStart, timeEnd, btnImg;
    EditText editTitle, editMemo;
    View dlgImage;

    //    디비
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white);

//        뷰
        editTitle = findViewById(R.id.editTitle);
        editMemo = findViewById(R.id.editMemo);

//        디비
        dbHelper = new DBHelper(this);

//        시작 시간 입력
        timeStart = findViewById(R.id.timeStart);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(WhiteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeStart.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, DateTime.nowHour(), DateTime.nowMinute(), true);

                timePickerDialog.setMessage("시작시간");
                timePickerDialog.show();
            }
        });

//        종료 시간 입력
        timeEnd = findViewById(R.id.timeEnd);
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(WhiteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeEnd.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, DateTime.nowHour(), DateTime.nowMinute(), true);

                timePickerDialog.setMessage("종료시간");
                timePickerDialog.show();

            }
        });

//        사진 추가
        btnImg = findViewById(R.id.btnImg);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 1);
            }
        });

//        일과 저장
        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTitle.getText().toString())) {
                    Toast.makeText(WhiteActivity.this, "일과 제목을 입력하세요.", Toast.LENGTH_SHORT).show();

//                    에딧 텍스트 포커스, 키보드 올리기
                    editTitle.requestFocus();
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    dbHelper.addOneul(MainActivity.showDay, timeStart.getText().toString(), timeEnd.getText().toString(),
                            editTitle.getText().toString(), editMemo.getText().toString(), 1);

                    Toast.makeText(getApplicationContext(), MainActivity.showDay + "\n일과를 저장했습니다.", Toast.LENGTH_SHORT).show();


                    finish();
                }
            }
        });

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == 1) {
//
//        }
//    }

    //    뒤로가기 시
    @Override
    public void onBackPressed() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setMessage("일과 작성을 취소합니다.\n작성한 내용은 저장되지 않습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#5E5E5E"));
            }
        });

        dialog.show();
    }
}

//    todo: 닫는 버튼이 필요
//    todo: 작성 화면에서도 날짜 바꿀 수 있게 해야 할듯