package com.oneul;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oneul.extra.DBHelper;

import java.util.Objects;

public class WriteActivity extends AppCompatActivity {
    //    ㄴㄴ 데이터
    int startHour, startMinute, endHour, endMinute;
    byte[] bytes;

    //    ㄴㄴ 뷰
    Button btnOk, timeStart, timeEnd, btnImg;
    EditText editTitle, editMemo;
    TextView t_oDate;
    LinearLayout ll_goCalendar;

    //    ㄴㄴ 디비
    DBHelper dbHelper;

    //    ㄴㄴ 캘린더
//    View calendarView;
//    MaterialCalendarView widget;
//    AlertDialog calendarDialog;


    @SuppressLint({"InflateParams", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

//        ㄴㄴ 뷰
        editTitle = findViewById(R.id.editTitle);
        editMemo = findViewById(R.id.editMemo);
        t_oDate = findViewById(R.id.t_oDate);

//        ㄴㄴ 디비
        dbHelper = DBHelper.getDB(this);

//        ㄴㄴ 캘린더
//        calendarView = getLayoutInflater().inflate(R.layout.view_calendar, null);
//        widget = calendarView.findViewById(R.id.mc_calendar);
////        최소 최대 날짜 설정
//        widget.state().edit()
//                .setMinimumDate(CalendarDay.from(2000, 1, 1))
//                .setMaximumDate(CalendarDay.from(2040, 1, 1))
//                .commit();
////        캘린더 헤더 수정
//        widget.setTitleFormatter(new TitleFormatter() {
//            @Override
//            public CharSequence format(CalendarDay day) {
//                return day.getDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월"));
//            }
//        });
//        widget.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                t_oDate.setText(Objects.requireNonNull(widget.getSelectedDate()).getDate().toString());
//                calendarDialog.dismiss();
//            }
//        });
//
////        캘린더 열기
//        ll_goCalendar = findViewById(R.id.ll_goCalendar);
//        ll_goCalendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                다이얼로그가 널이면
//                if (calendarDialog == null) {
//                    calendarDialog = DialogFragment.calendarDialog(WriteActivity.this, calendarView);
//                }
//
//                widget.setSelectedDate(LocalDate.parse(t_oDate.getText()));
//                calendarDialog.show();
//            }
//        });

//        시작 시간 입력
        timeStart = findViewById(R.id.timeStart);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(WriteActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeStart.setText(String.format("%02d:%02d", hourOfDay, minute));
                                startHour = hourOfDay;
                                startMinute = minute;
                            }
                        }, startHour, startMinute, true);
                timePickerDialog.setMessage("시작시간");
                timePickerDialog.show();
            }
        });

//        종료 시간 입력
        timeEnd = findViewById(R.id.timeEnd);
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(WriteActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeEnd.setText(String.format("%02d:%02d", hourOfDay, minute));
                                endHour = hourOfDay;
                                endMinute = minute;
                            }
                        }, endHour, endMinute, true);
                timePickerDialog.setMessage("종료시간");
                timePickerDialog.show();
            }
        });

//        사진 추가
        btnImg = findViewById(R.id.btnImg);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WriteActivity.this, CameraActivity.class));
            }
        });

//        일과 저장
        btnOk = findViewById(R.id.btnOk);
//        불러온 일과 있으면
        if (getIntent().getExtras() != null) {
            final String[] strings = getIntent().getExtras().getStringArray("editOneul");
            timeStart.setText(Objects.requireNonNull(strings)[2]);
            timeEnd.setText(strings[3]);
            editTitle.setText(strings[4]);
            editMemo.setText(strings[5]);

            startHour = Integer.parseInt(strings[2].substring(0, 2));
            startMinute = Integer.parseInt(strings[2].substring(3, 5));
            endHour = Integer.parseInt(strings[3].substring(0, 2));
            endMinute = Integer.parseInt(strings[3].substring(3, 5));

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    일과 제목 공백 체크
                    if (TextUtils.isEmpty(editTitle.getText().toString())) {
                        checkBlankTitle();
                    } else {
                        dbHelper.editOneul(Integer.parseInt(strings[0]), t_oDate.getText().toString(),
                                timeStart.getText().toString(), timeEnd.getText().toString(),
                                editTitle.getText().toString(), editMemo.getText().toString());

                        Toast.makeText(getApplicationContext(), t_oDate.getText() + "\n일과를 수정했습니다.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }
            });

//            불러온 일과 없으면
        } else {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    일과 제목 공백 체크
                    if (TextUtils.isEmpty(editTitle.getText().toString())) {
                        checkBlankTitle();
                    } else {

//                        fixme 사진 다중 추가로 바꾸기
//                        dbHelper.addOneul(t_oDate.getText().toString(), timeStart.getText().toString(),
//                                timeEnd.getText().toString(), editTitle.getText().toString(),
//                                editMemo.getText().toString(), bytes, 1);

                        Toast.makeText(getApplicationContext(), t_oDate.getText() + "\n일과를 저장했습니다.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }
            });
        }

//        시작 시
        t_oDate.setText(MainActivity.showDay);
    }

    //    일과 제목 공백 체크
    private void checkBlankTitle() {
        Toast.makeText(WriteActivity.this, "일과 제목을 입력하세요.", Toast.LENGTH_SHORT).show();

//        에딧 텍스트 포커스, 키보드 올리기
        editTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getApplicationContext())
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
    }

    //    에딧텍스트 언포커스
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        MainActivity.focusClear(this, ev);

        return super.dispatchTouchEvent(ev);
    }

    //    뒤로가기 시
    @Override
    public void onBackPressed() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
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

//        불러온 일과 있으면
        if (getIntent().getExtras() != null) {
            dialog.setMessage("일과 수정을 취소합니다.\n변경된 내용은 저장되지 않습니다.");

//        불러온 일과 없으면
        } else {
            dialog.setMessage("일과 작성을 취소합니다.\n작성한 내용은 저장되지 않습니다.");
        }

        dialog.show();
    }
}