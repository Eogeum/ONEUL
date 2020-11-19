package com.oneul;

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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.oneul.extra.BitmapRefactor;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.fragment.DialogFragment;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WriteActivity extends AppCompatActivity {
    //    ㄴㄴ 데이터
    boolean isEditMode, isEdited;
    int startHour, startMinute, endHour, endMinute;
    int oNo, photoCount;
    List<Bitmap> bitmaps = new ArrayList<>();

    //    ㄴㄴ 뷰
    Button dayStart, timeStart, dayEnd, timeEnd;
    EditText editTitle, editMemo;

    //    ㄴㄴ 기타
    DBHelper dbHelper;
    InputMethodManager imm;
    AlertDialog startDialog, endDialog, closeDialog;

    //    ㄴㄴ 이미지뷰어
    LinearLayout ll_imagePreview;
    StfalconImageViewer<Bitmap> viewer;

    @SuppressLint({"InflateParams", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

//        ㄴㄴ 데이터
        isEdited = false;

//        ㄴㄴ 뷰
        ll_imagePreview = findViewById(R.id.ll_imagePreview);

//        ㄴㄴ 기타
        dbHelper = DBHelper.getDB(this);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        dayStart = findViewById(R.id.dayStart);
        dayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdited = true;
                startDialog.show();
            }
        });

        dayEnd = findViewById(R.id.dayEnd);
        dayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdited = true;
                endDialog.show();
            }
        });

        startDialog = DialogFragment.calendarDialog(WriteActivity.this, dayStart);
        endDialog = DialogFragment.calendarDialog(WriteActivity.this, dayEnd);

//        시작 시간 입력
        timeStart = findViewById(R.id.timeStart);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdited = true;

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
                isEdited = true;

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

        editTitle = findViewById(R.id.editTitle);
        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isEdited = true;
            }
        });
        editMemo = findViewById(R.id.editMemo);
        editMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isEdited = true;
            }
        });

        //        불러온 일과 있으면
        if (getIntent().getExtras() != null) {
            isEditMode = true;

            String[] strings = getIntent().getExtras().getStringArray("editOneul");
            String startTime = DateTime.subToTime(Objects.requireNonNull(strings)[1]);
            String endTime = DateTime.subToTime(strings[2]);

            dayStart.setText(DateTime.subToDay(strings[1]));
            timeStart.setText(startTime);
            dayEnd.setText(DateTime.subToDay(strings[2]));
            timeEnd.setText(endTime);
            editTitle.setText(strings[3]);
            editMemo.setText(strings[4]);

            startHour = Integer.parseInt(startTime.substring(0, 2));
            startMinute = Integer.parseInt(startTime.substring(3, 5));
            endHour = Integer.parseInt(endTime.substring(0, 2));
            endMinute = Integer.parseInt(endTime.substring(3, 5));

            oNo = Integer.parseInt(strings[0]);
            photoCount = dbHelper.getPhotoCount(oNo) + 1;
            bitmaps = dbHelper.getPhotos(oNo);
            refreshImageViewer();

//            불러온 일과 없으면
        } else {
            isEditMode = false;
        }

        findViewById(R.id.i_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdited) {
                    createCloseDialog();
                    closeDialog.show();
                } else {
                    finish();
                }
            }
        });

        findViewById(R.id.i_ok).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    일과 제목 공백 체크
                        if (TextUtils.isEmpty(editTitle.getText().toString())) {
                            checkBlankTitle();
                        } else {
//                    불러온 일과가 있으면
                            if (isEditMode) {
                                dbHelper.editOneul(oNo, dayStart.getText() + " " + timeStart.getText(),
                                        dayEnd.getText() + " " + timeEnd.getText(),
                                        editTitle.getText().toString(), editMemo.getText().toString());
                                dbHelper.editPhoto(oNo, bitmaps);

                                Toast.makeText(WriteActivity.this, dayStart.getText() + "\n일과를 수정했습니다.",
                                        Toast.LENGTH_SHORT).show();

//                    불러온 일과가 없으면
                            } else {
                                dbHelper.startOneul(dayStart.getText() + " " + timeStart.getText(),
                                        dayEnd.getText() + " " + timeEnd.getText(),
                                        editTitle.getText().toString(), editMemo.getText().toString(), bitmaps, 1);
                                dbHelper.editPhoto(oNo, bitmaps);

                                Toast.makeText(WriteActivity.this, dayStart.getText() + "\n일과를 저장했습니다.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    }
                });

        findViewById(R.id.ll_pictureMemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdited = true;

                if (photoCount < 5) {
                    if (DialogFragment.permissionCheck(WriteActivity.this, true)) {
                        DialogFragment.addPhotoDialog(WriteActivity.this);
                    }
                } else {
                    Toast.makeText(WriteActivity.this, "최대 5장까지만 추가가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void refreshImageViewer() {
        if (photoCount > 0) {
            ll_imagePreview.removeAllViews();

            for (int i = 0; i < photoCount; i++) {
                final ImageView imageView = new ImageView(WriteActivity.this);
                imageView.setImageBitmap(bitmaps.get(i));
                imageView.setAdjustViewBounds(true);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View view = View.inflate(WriteActivity.this, R.layout.view_overlay, null);

                        view.findViewById(R.id.ll_deletePhoto).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment.deletePhotoDialog(WriteActivity.this, viewer, bitmaps);

                                photoCount = bitmaps.size();
                                ll_imagePreview.removeViewAt(viewer.currentPosition());
                            }
                        });

                        view.findViewById(R.id.ll_downloadPhoto).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment.downloadPhoto(WriteActivity.this,
                                        BitmapRefactor.bitmapToBytes(bitmaps.get(viewer.currentPosition())));
                            }
                        });

                        viewer = new StfalconImageViewer.Builder<>(WriteActivity.this, bitmaps,
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

                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 16, 0);
                imageView.setLayoutParams(params);
                imageView.setBackground(getDrawable(R.drawable.home_list_ll));
                imageView.setClipToOutline(true);

                ll_imagePreview.addView(imageView);
            }

            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            ll_imagePreview.getChildAt(ll_imagePreview.getChildCount() - 1).setLayoutParams(params);
        }
    }

    private void createCloseDialog() {
        if (closeDialog == null) {
            closeDialog = new AlertDialog.Builder(WriteActivity.this)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();

            closeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    closeDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
                    closeDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#5E5E5E"));
                }
            });

//                    불러온 일과 있으면
            if (isEditMode) {
                closeDialog.setMessage("일과 수정을 취소합니다.\n변경된 내용은 저장되지 않습니다.");

//                    불러온 일과 없으면
            } else {
                closeDialog.setMessage("일과 작성을 취소합니다.\n작성한 내용은 저장되지 않습니다.");
            }
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
        if (isEdited) {
            createCloseDialog();
            closeDialog.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
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
                                if (bitmaps.size() < 5) {
                                    bitmaps.add(BitmapRefactor.uriToBitmap(this, clipData.getItemAt(i).getUri()));
                                } else {
                                    Toast.makeText(this, "최대 5장까지만 추가가능합니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        } else {
                            bitmaps.add(BitmapRefactor.uriToBitmap(this, data.getData()));
                        }
                    }
                    break;
            }

            photoCount = bitmaps.size();
            refreshImageViewer();

            Toast.makeText(this, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (DialogFragment.permissionCheck(this, false)) {
            DialogFragment.addPhotoDialog(this);
        } else {
            finish();
        }
    }
}