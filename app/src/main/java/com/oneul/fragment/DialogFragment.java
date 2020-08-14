package com.oneul.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.oneul.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class DialogFragment extends Fragment {
    static CalendarDay selectDay;

    public static void editMemoDialog(final Activity activity, final int bottomButtonId) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setMessage("메모 작성을 취소합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.findViewById(R.id.btn_cancelMemo).callOnClick();

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

    public static void UploadImageDialog(final Activity activity, int re) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            final AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setMessage("사진 첨부를 위해 저장소 권한이 필요합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        }
                    })
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E88346"));
                }
            });

            dialog.show();
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("앱 선택")
                    .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent chooserIntent = null;

                            switch (i) {
                                case 0:
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    chooserIntent = Intent.createChooser(cameraIntent, "앱 선택");
                                    activity.startActivityForResult(chooserIntent, 1);
                                    break;

                                case 1:
                                    Intent galleryIntent = new Intent();
                                    galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                    chooserIntent = Intent.createChooser(galleryIntent, "앱 선택");
                                    activity.startActivityForResult(chooserIntent, 1);
                                    break;
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }
}