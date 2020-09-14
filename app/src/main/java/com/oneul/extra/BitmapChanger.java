package com.oneul.extra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.oneul.fragment.DialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapChanger {
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap getBitmap(Uri uri, Context context) {
        Bitmap bitmap = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    public static Bitmap checkAndResize(Bitmap bitmap) {
        byte[] bytes = BitmapChanger.bitmapToBytes(bitmap);

        if (bytes.length > 1000 * 1024) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 4;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }

        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void resultToDB(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;

            switch (requestCode) {
                case DialogFragment.CAMERA_REQUEST_CODE:
//                    미디어 스캔
                    MediaScannerConnection.scanFile(activity, new String[]{DialogFragment.photoPath},
                            null, null);

                    bitmap = BitmapFactory.decodeFile(DialogFragment.photoPath);
                    try {
                        ExifInterface ei = new ExifInterface(DialogFragment.photoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bitmap = rotateBitmap(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bitmap = rotateBitmap(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bitmap = rotateBitmap(bitmap, 270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case DialogFragment.GALLERY_REQUEST_CODE:
                    bitmap = getBitmap(data.getData(), activity);
                    break;
            }

            bitmap = checkAndResize(bitmap);
            DBHelper dbHelper = DBHelper.getDB(activity);
            dbHelper.addPhoto(dbHelper.getStartOneul().getoNo(), bitmapToBytes(bitmap));
            Toast.makeText(activity, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}