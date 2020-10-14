package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.calendar.OneulDecorator;
import com.oneul.extra.DBHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class DialogFragment {
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 202;

    public static String photoPath;
    private static AlertDialog dialog;

    public static void checkMemoDialog(final Activity activity, final int bottomButtonId) {
        dialog = new AlertDialog.Builder(activity)
                .setMessage("메모 작성을 취소합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.findViewById(R.id.ll_cancelMemo).callOnClick();

                        if (bottomButtonId != 0) {
                            activity.findViewById(bottomButtonId).callOnClick();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
            }
        });
        dialog.show();
    }

    public static AlertDialog calendarDialog(final Activity activity) {
        final View view = View.inflate(activity, R.layout.view_calendar, null);
        final MaterialCalendarView mc_calendar = view.findViewById(R.id.mc_calendar);

        final DBHelper dbHelper = DBHelper.getDB(activity);

//        최소 최대 날짜 설정
        mc_calendar.state().edit()
                .setMinimumDate(CalendarDay.from(2000, 1, 1))
                .setMaximumDate(CalendarDay.from(2040, 1, 1))
                .commit();
//        캘린더 헤더 수정
        mc_calendar.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월"));
            }
        });
        mc_calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                MainActivity.showDay = Objects.requireNonNull(widget.getSelectedDate()).getDate().toString();
                dialog.cancel();
            }
        });

        dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mc_calendar.removeDecorators();
                mc_calendar.addDecorators(new OneulDecorator(dbHelper.getOneulDates()));
                mc_calendar.setSelectedDate(LocalDate.parse(MainActivity.showDay));
            }
        });

        return dialog;
    }

//    fixme 다중
//    public static View imageViewer(final Context context, final StfalconImageViewer<Bitmap> viewer,
//                                   final int oNo, final int pNo) {
//        final DBHelper dbHelper = DBHelper.getDB(context);
//
//        View overlay = View.inflate(context, R.layout.view_overlay, null);
//        LinearLayout ll_deletePhoto = overlay.findViewById(R.id.ll_deletePhoto);
//        ll_deletePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePhotoDialog(context, viewer, oNo, dbHelper.getpNos(oNo).get(0));
//            }
//        });
//
//        LinearLayout ll_downloadPhoto = overlay.findViewById(R.id.ll_downloadPhoto);
//        ll_downloadPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //        todo 효율적인 퍼미션 체크로 변경
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
//                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
//                    FileOutputStream stream = null;
//                    try {
//                        stream = new FileOutputStream(DialogFragment.getFile("/Download"));
//                        stream.write(dbHelper.getPhoto(dbHelper.getpNos(oNo).get(0)));
//                        stream.flush();
//                        stream.close();
//
//                        Toast.makeText(context, "\"/Download\"에 저장했습니다.", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }, 1);
//                }
//            }
//        });
//
//        return null;
//    }

    public static void addPhotoDialog(final Activity activity) {
        dialog = new AlertDialog.Builder(activity)
                .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
//                            카메라 선택 시
                            case 0:
//                                임시 파일 만들기
                                File file = getFile("/DCIM");
                                photoPath = file.getAbsolutePath();

//                                카메라 인텐트
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                                    Uri photoURI = FileProvider.getUriForFile(activity,
                                            "com.oneul.fileprovider", file);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                }

                                activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                break;

//                            갤러리 선택 시
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK)
                                        .setType("image/*")
                                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                        .setType(MediaStore.Images.Media.CONTENT_TYPE);

                                activity.startActivityForResult(galleryIntent,
                                        GALLERY_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        dialog.show();
    }

    public static void deletePhotoDialog(final Context context, final StfalconImageViewer<Bitmap> viewer,
                                         final int oNo, final int pNo) {
        dialog = new AlertDialog.Builder(context)
                .setMessage("사진을 삭제합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DBHelper dbHelper = DBHelper.getDB(context);
                        dbHelper.deletePhoto(pNo);
                        viewer.updateImages(dbHelper.getPhotos(oNo));

                        Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
            }
        });
        dialog.show();
    }


    @SuppressLint("SimpleDateFormat")
    public static File getFile(String folder) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(System.currentTimeMillis()) + ".jpg";
        File fileDir = new File(Environment.getExternalStorageDirectory() + folder, "O:NEUL");

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return new File(fileDir, fileName);
    }
}