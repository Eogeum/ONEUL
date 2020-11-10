package com.oneul;

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
import androidx.exifinterface.media.ExifInterface;

import com.oneul.extra.BitmapRefactor;
import com.oneul.extra.DBHelper;
import com.oneul.fragment.DialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    DBHelper dbHelper;
    int oNo, photoCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = DBHelper.getDB(this);
        oNo = dbHelper.getStartOneul().getoNo();
        photoCount = dbHelper.getPhotoCount(oNo) + 1;

        if (photoCount < 5) {
            if (DialogFragment.permissionCheck(this, true)) {
                DialogFragment.addPhotoDialog(this);
            }
        } else {
            Toast.makeText(this, "최대 5장까지만 추가가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

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
                                if (photoCount + i < 5) {
                                    bitmaps.add(BitmapRefactor.uriToBitmap(this, clipData.getItemAt(i).getUri()));
                                } else {
                                    break;
                                }
                            }
                        } else {
                            bitmaps.add(BitmapRefactor.uriToBitmap(this, data.getData()));
                        }
                    }
                    break;
            }

            dbHelper.addPhoto(dbHelper.getStartOneul().getoNo(), bitmaps);
            Toast.makeText(this, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
        }

        finish();
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