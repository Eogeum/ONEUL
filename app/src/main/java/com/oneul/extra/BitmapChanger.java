package com.oneul.extra;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

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

    public static Bitmap getBitmap(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
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

    public static void bitmapToDB(Activity activity, Bitmap bitmap) {
        bitmap = checkAndResize(bitmap);
        DBHelper dbHelper = DBHelper.getDB(activity);
        dbHelper.addPhoto(dbHelper.getStartOneul().getoNo(), bitmapToBytes(bitmap));

        Toast.makeText(activity, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
    }
}