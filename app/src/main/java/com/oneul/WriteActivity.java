package com.oneul;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.oneul.extra.BitmapRefactor;
import com.oneul.extra.DBHelper;
import com.oneul.fragment.DialogFragment;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WriteActivity extends AppCompatActivity {
    //    ㄴㄴ 데이터
    int startHour, startMinute, endHour, endMinute;
    List<Bitmap> bitmaps;

    //    ㄴㄴ 뷰
    Button btnOk, timeStart, timeEnd;
    EditText editTitle, editMemo;
    TextView t_oDate;
    LinearLayout ll_pictureMemo;

    //    ㄴㄴ 기타
    DBHelper dbHelper;
    InputMethodManager imm;
    AlertDialog calendarDialog;

    //    ㄴㄴ 이미지뷰어
    LinearLayout ll_imagePreview;
    StfalconImageViewer<Bitmap> viewer;

    @SuppressLint({"InflateParams", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

//        ㄴㄴ 뷰
        editTitle = findViewById(R.id.editTitle);
        editMemo = findViewById(R.id.editMemo);
        btnOk = findViewById(R.id.btnOk);
        ll_pictureMemo = findViewById(R.id.ll_pictureMemo);
        ll_imagePreview = findViewById(R.id.ll_imagePreview);

//        ㄴㄴ 기타
        dbHelper = DBHelper.getDB(this);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        t_oDate = findViewById(R.id.t_oDate);
        t_oDate.setText(MainActivity.showDay);
        calendarDialog = DialogFragment.calendarDialog(this, t_oDate, dbHelper.getOneulDates());
        findViewById(R.id.ll_goCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDialog.show();
            }
        });

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

            final int oNo = Integer.parseInt(strings[0]);
            int photoCount = dbHelper.getPhotoCount(oNo) + 1;

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    일과 제목 공백 체크
                    if (TextUtils.isEmpty(editTitle.getText().toString())) {
                        checkBlankTitle();
                    } else {
                        dbHelper.editOneul(oNo, t_oDate.getText().toString(),
                                timeStart.getText().toString(), timeEnd.getText().toString(),
                                editTitle.getText().toString(), editMemo.getText().toString());

                        finish();
                    }
                }
            });

//            fixme 사진 기능 수정필요
            ll_pictureMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment.permissionCheck(WriteActivity.this, dbHelper.getStartOneul().getoNo());
                }
            });

//            디비에서 사진 불러오기
            if (photoCount > 0) {
                ll_imagePreview.removeAllViews();

                for (int i = 0; i < photoCount; i++) {
                    final ImageView imageView = new ImageView(WriteActivity.this);
                    imageView.setImageBitmap(dbHelper.getPhotos(oNo).get(i));
                    imageView.setAdjustViewBounds(true);
                    imageView.setPadding(0, 0, 16, 0);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final View view = View.inflate(WriteActivity.this, R.layout.view_overlay, null);

                            view.findViewById(R.id.ll_deletePhoto).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogFragment.deletePhotoDialog(WriteActivity.this, viewer, oNo,
                                            dbHelper.getpNos(oNo).get(viewer.currentPosition()));

                                    ll_imagePreview.removeViewAt(viewer.currentPosition());
                                }
                            });

                            view.findViewById(R.id.ll_downloadPhoto).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogFragment.downloadPhoto(WriteActivity.this,
                                            dbHelper.getpNos(oNo).get(viewer.currentPosition()));
                                }
                            });

                            viewer = new StfalconImageViewer.Builder<>(WriteActivity.this, dbHelper.getPhotos(oNo),
                                    new ImageLoader<Bitmap>() {
                                        @Override
                                        public void loadImage(ImageView imageView, Bitmap image) {
                                            imageView.setImageBitmap(image);
                                        }
                                    })
                                    .withStartPosition(ll_imagePreview.indexOfChild(imageView))
                                    .withOverlayView(view)
                                    .build();

                            viewer.show();
                        }
                    });

                    if (i == photoCount - 1) {
                        imageView.setPadding(0, 0, 0, 0);
                    }

                    ll_imagePreview.addView(imageView);
                }
            }

//            불러온 일과 없으면
        } else {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    일과 제목 공백 체크
                    if (TextUtils.isEmpty(editTitle.getText().toString())) {
                        checkBlankTitle();
                    } else {
                        dbHelper.startOneul(t_oDate.getText().toString(), timeStart.getText().toString(),
                                timeEnd.getText().toString(), editTitle.getText().toString(),
                                editMemo.getText().toString(), bitmaps, 1);

                        Toast.makeText(getApplicationContext(), t_oDate.getText() + "\n일과를 저장했습니다.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }
            });

            ll_pictureMemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment.permissionCheck(WriteActivity.this, 0);
                }
            });
        }
    }

    //    일과 제목 공백 체크
    private void checkBlankTitle() {
        Toast.makeText(WriteActivity.this, "일과 제목을 입력하세요.", Toast.LENGTH_SHORT).show();

//        에딧 텍스트 포커스, 키보드 올리기
        editTitle.requestFocus();
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

    //            fixme 사진 기능 수정필요
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            List<Bitmap> bitmaps = new ArrayList<>();

            switch (requestCode) {
                case DialogFragment.CAMERA_REQUEST_CODE:
//                    check 미디어 스캔 최적화
                    MediaScannerConnection.scanFile(this, new String[]{DialogFragment.photoPath},
                            null, null);

                    Bitmap bitmap = BitmapFactory.decodeFile(DialogFragment.photoPath);
                    try {
                        ExifInterface ei = new ExifInterface(DialogFragment.photoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bitmap = BitmapRefactor.rotateBitmap(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bitmap = BitmapRefactor.rotateBitmap(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bitmap = BitmapRefactor.rotateBitmap(bitmap, 270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmaps.add(bitmap);
                    break;

                case DialogFragment.GALLERY_REQUEST_CODE:
                    if (data != null) {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                bitmaps.add(BitmapRefactor.uriToBitmap(this, clipData.getItemAt(i).getUri()));
                            }
                        } else {
                            bitmaps.add(BitmapRefactor.uriToBitmap(this, data.getData()));
                        }
                    }
                    break;
            }

//            fixme 사진 추가하면 리스트에 넣기
            dbHelper.addPhoto(this, dbHelper.getStartOneul().getoNo(), bitmaps);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        check 퍼미션 체크 최적화
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            DialogFragment.addPhotoDialog(this);
        } else {
            finish();
        }
    }
}