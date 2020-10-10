package com.oneul;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.oneul.extra.BitmapRefactor;
import com.oneul.fragment.DialogFragment;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogFragment.checkPermissionDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;

            switch (requestCode) {
                case DialogFragment.CAMERA_REQUEST_CODE:
//                    미디어 스캔
                    MediaScannerConnection.scanFile(this, new String[]{DialogFragment.photoPath},
                            null, null);

                    bitmap = BitmapFactory.decodeFile(DialogFragment.photoPath);
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

                    BitmapRefactor.bitmapToDB(this, bitmap);
                    break;

                case DialogFragment.GALLERY_REQUEST_CODE:
                    if (data != null) {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                bitmap = BitmapRefactor.uriToBitmap(this, clipData.getItemAt(i).getUri());
                                BitmapRefactor.bitmapToDB(this, bitmap);
                            }
                        } else {
                            bitmap = BitmapRefactor.uriToBitmap(this, data.getData());
                            BitmapRefactor.bitmapToDB(this, bitmap);
                        }
                    }
                    break;
            }

            Toast.makeText(this, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        todo 효율적인 퍼미션 체크로 변경
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            DialogFragment.addPhotoDialog(this);
        } else {
            finish();
        }
    }
}