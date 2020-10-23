package com.oneul.extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.MainActivity;
import com.oneul.fragment.HomeFragment;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //    일과 테이블 정보
    public static final String TABLE_ONEUL = "Oneul";
    public static final String COLUMN_ONO = "oNo";
    public static final String COLUMN_ODATE = "oDate";
    public static final String COLUMN_OSTART = "oStart";
    public static final String COLUMN_OEND = "oEnd";
    public static final String COLUMN_OTITLE = "oTitle";
    public static final String COLUMN_OMEMO = "oMemo";
    public static final String COLUMN_ODONE = "oDone";

    //    사진 테이블 정보
    public static final String TABLE_PHOTO = "Photo";
    public static final String COLUMN_PNO = "pNo";
    public static final String COLUMN_PPHOTO = "pPhoto";

    //    디비 정보
    private static final String DATABASE_ONEUL = "OneulDB";
    private static final int DATABASE_VERSION = 1;

    //     테이블 생성
    private static final String CREATE_ONEUL = "CREATE TABLE " + TABLE_ONEUL + "(" +
            COLUMN_ONO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ODATE + " TEXT NOT NULL, " +
            COLUMN_OSTART + " TEXT, " +
            COLUMN_OEND + " TEXT, " +
            COLUMN_OTITLE + " TEXT NOT NULL, " +
            COLUMN_OMEMO + " TEXT, " +
            COLUMN_ODONE + " INTEGER NOT NULL);";
    private static final String CREATE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + "(" +
            COLUMN_PNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PPHOTO + " BLOB, " +
            COLUMN_ONO + " INTEGER, " +

            "CONSTRAINT p_o_oNo FOREIGN KEY(" + COLUMN_ONO + ") REFERENCES " + TABLE_ONEUL + "(" + COLUMN_ONO + "));";

    //    디비
    private static DBHelper dbHelper;

    //    디비 생성자
    private DBHelper(Context context) {
        super(context, DATABASE_ONEUL, null, DATABASE_VERSION);
    }

    //    디비 만들기
    public static synchronized DBHelper getDB(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context.getApplicationContext());
        }

        return dbHelper;
    }

    //    ㄴㄴ 일과
    //    일과 시작
    public void startOneul(Context context, String oDate, String oStart, String oEnd, String oTitle,
                           String oMemo, @Nullable List<Bitmap> pPhotos, int oDone) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ODATE, oDate);
        values.put(DBHelper.COLUMN_OSTART, oStart);
        values.put(DBHelper.COLUMN_OEND, oEnd);
        values.put(DBHelper.COLUMN_OTITLE, oTitle);
        values.put(DBHelper.COLUMN_OMEMO, oMemo);
        values.put(DBHelper.COLUMN_ODONE, oDone);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ONEUL, null, values);

        if (pPhotos != null) {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_ONEUL, new String[]{COLUMN_ONO}, COLUMN_ODONE + " = 1",
                    null, null, null, COLUMN_ONO + " DESC");

            if (cursor.moveToFirst()) {
                for (int i = 0; i < pPhotos.size(); i++) {
                    addPhoto(context, cursor.getInt(cursor.getColumnIndex(COLUMN_ONO)),
                            Collections.singletonList(pPhotos.get(i)));
                }
            }

            cursor.close();
        }
    }

    //    기록중인 일과 불러오기
    public Oneul getStartOneul() {
        Oneul oneul = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ODONE + " = 0",
                null, null, null, null);

        while (cursor.moveToNext()) {
            oneul = new Oneul(cursor.getInt(cursor.getColumnIndex(COLUMN_ONO)),
                    cursor.getString(cursor.getColumnIndex((COLUMN_ODATE))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OSTART)),
                    null,
                    cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO)),
                    null);
        }

        cursor.close();

        return oneul;
    }

    //    완료 일과 불러오기
    public void getOneul(String oDate, RecyclerView r_oneul, OneulAdapter adapter, String sort) {
        sort = " " + sort;
        adapter.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ODATE + " = ? AND " + COLUMN_ODONE + " = 1",
                new String[]{oDate}, null, null, COLUMN_OSTART + sort + ", " + COLUMN_ONO + sort);

        while (cursor.moveToNext()) {
            byte[] pPhoto = null;
            int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));

            Cursor photoCursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                    null, null, null, COLUMN_PNO + " ASC");

            if (photoCursor.moveToFirst()) {
                pPhoto = photoCursor.getBlob(photoCursor.getColumnIndex(COLUMN_PPHOTO));
            }

            photoCursor.close();

            adapter.addItem(
                    new Oneul(oNo,
                            oDate,
                            cursor.getString(cursor.getColumnIndex(COLUMN_OSTART)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_OEND)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO)),
                            pPhoto));
        }

        cursor.close();

        r_oneul.setAdapter(adapter);
    }


    //   수정할 일과 불러오기
    public String[] getOneul(int oNo) {
        String[] strings = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            strings = new String[]{String.valueOf(oNo),
                    cursor.getString(cursor.getColumnIndex((COLUMN_ODATE))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OSTART)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OEND)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO))};
        }

        cursor.close();

        return strings;
    }

    //    일과 수정
    public void editOneul(int oNo, String oDate, String oStart, String oEnd, String oTitle, String oMemo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ODATE, oDate);
        values.put(DBHelper.COLUMN_OSTART, oStart);
        values.put(DBHelper.COLUMN_OEND, oEnd);
        values.put(DBHelper.COLUMN_OTITLE, oTitle);
        values.put(DBHelper.COLUMN_OMEMO, oMemo);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
    }

    //    일과 기록 종료
    public void endOneul(int oNo, String oEnd) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_OEND, oEnd);
        values.put(DBHelper.COLUMN_ODONE, 1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
    }

    //    일과 삭제
    public void deleteOneul(int oNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_ONO + " = " + oNo, null);
        db.delete(TABLE_ONEUL, COLUMN_ONO + " = " + oNo, null);
    }

    public void refreshRecyclerView() {
//        오늘이면
        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
            getOneul(MainActivity.showDay, HomeFragment.r_oneul, HomeFragment.adapter, "DESC");

//        오늘이 아니면
        } else {
            getOneul(MainActivity.showDay, HomeFragment.r_oneul, HomeFragment.adapter, "ASC");
        }
    }

    //    ㄴㄴ 사진
    //    일과 사진 가져오기
    public byte[] getPhoto(int pNo) {
        byte[] bytes = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_PNO + " = " + pNo,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PPHOTO));
        }

        cursor.close();

        return bytes;
    }

    //    일과 사진 여러개 가져오기
    public List<Bitmap> getPhotos(int oNo) {
        List<Bitmap> bitmaps = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                null, null, null, COLUMN_PNO + " ASC");

        while (cursor.moveToNext()) {
            bitmaps.add(BitmapRefactor.bytesToBitmap(cursor.getBlob(cursor.getColumnIndex(COLUMN_PPHOTO))));
        }

        cursor.close();

        return bitmaps;
    }

    //    일과 사진 번호 여러개 가져오기
    public List<Integer> getpNos(int oNo) {
        List<Integer> integers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PNO}, COLUMN_ONO + " = " + oNo,
                null, null, null, COLUMN_PNO + " ASC");

        while (cursor.moveToNext()) {
            integers.add(cursor.getInt(cursor.getColumnIndex(COLUMN_PNO)));
        }

        cursor.close();

        return integers;
    }

    public int getPhotoCount(int oNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PNO}, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        int photoCount = cursor.getCount() - 1;

        cursor.close();

        return photoCount;
    }

    //    일과 사진 추가
    public void addPhoto(Context context, int oNo, List<Bitmap> pPhotos) {
        int photoCount;

        if (getOneul(oNo) != null) {
            photoCount = getPhotoCount(oNo) + 1;
        } else {
            photoCount = 0;
        }

        for (int i = 0; i < pPhotos.size(); i++) {
            if (photoCount >= 5) {
                Toast.makeText(context, "최대 5장까지만 추가가능합니다.", Toast.LENGTH_SHORT).show();
                break;
            } else {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_ONO, oNo);
                values.put(DBHelper.COLUMN_PPHOTO, BitmapRefactor.bitmapToBytes(
                        BitmapRefactor.resizeBitmap(pPhotos.get(i))));

                SQLiteDatabase db = this.getWritableDatabase();
                db.insert(TABLE_PHOTO, null, values);

                photoCount++;
            }

            if (photoCount == 5) {
                Toast.makeText(context, "사진을 추가했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    일과 사진 삭제
    public void deletePhoto(int pNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_PNO + " = " + pNo, null);
    }

    //    ㄴㄴ 메모
    //    일과 메모 수정
    public void editMemo(int oNo, String oMemo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_OMEMO, oMemo);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
    }

    //    ㄴㄴ 캘린더
    //    일과 날짜 불러오기
    public List<CalendarDay> getOneulDates() {
        ArrayList<CalendarDay> calendarDays = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ONEUL, new String[]{COLUMN_ODATE}, COLUMN_ODONE + " = 1",
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            calendarDays.add(CalendarDay.from(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_ODATE)))));
        }

        cursor.close();

        return calendarDays;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONEUL);
        db.execSQL(CREATE_ONEUL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL(CREATE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONEUL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        this.onCreate(db);
    }
}