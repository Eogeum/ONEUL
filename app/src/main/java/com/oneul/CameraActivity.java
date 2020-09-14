package com.oneul;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.oneul.extra.BitmapChanger;
import com.oneul.fragment.DialogFragment;

import java.io.File;
import java.text.SimpleDateFormat;

public class CameraActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 202;

    private static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        권한 없다면
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 0 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != 0 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            dialog = new AlertDialog.Builder(this)
                    .setMessage("사진 첨부를 위해 카메라 및\n저장소 권한이 필요합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{
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

//            권한 있다면
        } else {
            new AlertDialog.Builder(this)
                    .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
//                            카메라 선택 시
                                case 0:
//                                임시 파일 만들기
                                    String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
                                            .format(System.currentTimeMillis()) + ".jpg";
                                    File fileDir = new File(Environment.getExternalStorageDirectory() +
                                            "/DCIM", "O:NEUL");
                                    if (!fileDir.exists()) {
                                        fileDir.mkdirs();
                                    }
                                    File file = new File(fileDir, fileName);
                                    DialogFragment.photoPath = file.getAbsolutePath();

//                                카메라 인텐트
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                        Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
                                                "com.oneul.fileprovider", file);
                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    }

                                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                    break;

//                            갤러리 선택 시
                                case 1:
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK)
                                            .setType("image/*")
                                            .setType(MediaStore.Images.Media.CONTENT_TYPE);

                                    startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                    break;
                            }
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BitmapChanger.resultToDB(this, requestCode, resultCode, data);
        Toast.makeText(this, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        todo 효율적인 퍼미션 체크로 변경
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            DialogFragment.selectorDialog(this);
        }
    }
}