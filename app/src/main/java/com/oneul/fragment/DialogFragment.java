package com.oneul.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.oneul.CameraActivity;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class DialogFragment {
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 202;

    public static String photoPath;
    private static AlertDialog dialog;

    public static AlertDialog calendarDialog(final Context context, final TextView t_oDate) {
        View v_calendar = View.inflate(context, R.layout.view_calendar, null);

        final AlertDialog calendarDialog = new AlertDialog.Builder(context)
                .setView(v_calendar)
                .create();

        final MaterialCalendarView mc_calendar = v_calendar.findViewById(R.id.mc_calendar);
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
        mc_calendar
                .setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        if (context instanceof MainActivity) {
                            MainActivity.showDay = Objects.requireNonNull(widget.getSelectedDate()).getDate().toString();
                        } else {
                            t_oDate.setText(Objects.requireNonNull(widget.getSelectedDate()).getDate().toString());
                        }

                        calendarDialog.cancel();
                    }
                });

        calendarDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mc_calendar.removeDecorators();
                mc_calendar.addDecorators(new OneulDecorator(DBHelper.getDB(context).getOneulDates()));
                mc_calendar.setSelectedDate(LocalDate.parse(t_oDate.getText()));
            }
        });

        return calendarDialog;
    }

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

        if (activity instanceof CameraActivity) {
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    activity.finish();
                }
            });
        }

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

    public static void deletePhotoDialog(final Context context, final StfalconImageViewer<Bitmap> viewer,
                                         final List<Bitmap> bitmaps) {
        dialog = new AlertDialog.Builder(context)
                .setMessage("사진을 삭제합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bitmaps.remove(viewer.currentPosition());
                        viewer.updateImages(bitmaps);

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

    public static void downloadPhoto(Context context, byte[] bytes) {
        //        check 퍼미션 체크 최적화
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            try {
                File file = getFile("/Download");
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(bytes);
                stream.flush();
                stream.close();

//                check 미디어 스캔 최적화
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()},
                        null, null);

//                todo 알림으로 바꾸기
                Toast.makeText(context, "\"/Download\"에 저장했습니다.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("SimpleDateFormat")
    public static File getFile(String folder) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(System.currentTimeMillis()) + ".jpg";
        File fileDir = new File(Environment.getExternalStorageDirectory() + folder, "O:NEUL");

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return new File(fileDir, fileName);
    }

    public static void permissionCheck(Context context) {
        //        check 퍼미션 체크 최적화
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == 0 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            addPhotoDialog(((Activity) context));
        } else {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }
}