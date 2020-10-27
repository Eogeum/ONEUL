package com.oneul.extra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapRefactor {
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } else {
                return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        byte[] bytes = BitmapRefactor.bitmapToBytes(bitmap);

        if (bytes.length > 1000 * 1024) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 4;
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }

        return bitmap;
    }
}