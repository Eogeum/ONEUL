package com.oneul.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.oneul.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DialogFragment {
    public static void editMemoDialog(final Activity activity, final int bottomButtonId) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
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
                        dialog.dismiss();
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

    public static AlertDialog calendarDialog(Activity activity, View view) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();

        return dialog;
    }

    //    fixme later
    public static String currentPhotoPath;

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
//                                카메라 선택 시
                                case 0:
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                                        File photoFile = null;
                                        try {
                                            photoFile = createImageFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

//                                        촬영한 사진이 있다면
                                        if (photoFile != null) {
                                            Uri photoURI = FileProvider.getUriForFile(activity, "com.oneul.fileprovider", photoFile);
                                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                                            activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                        }
                                    }
                                    break;

                                case 1:
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                                    galleryIntent.setType("image/*");
                                    galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                                    activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }

    private static File createImageFile() throws IOException {
        String imageFileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(System.currentTimeMillis()) + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM", "O:NEUL");

//        폴더가 없으면
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File imageFile = new File(storageDir, imageFileName);
        currentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }
}