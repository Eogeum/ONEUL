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

public class DialogFragment extends Fragment {
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

    public static void UploadImageDialog(final Activity activity) {
//        권한 없을 시
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            final AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setMessage("사진 첨부를 위해 카메라 및\n저장소 권한이 필요합니다.")
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
            final int CAMERA_REQUEST_CODE = 101;
            final int GALLERY_REQUEST_CODE = 202;

            new AlertDialog.Builder(activity)
                    .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                    break;

                                case 1:
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                                    galleryIntent.setType("image/*");
                                    activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }
}