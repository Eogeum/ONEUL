package com.oneul.extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //    디비
    private static DBHelper dbHelper;

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

    //    일과 추가
    public void addOneul(String oDate, String oStart, String oEnd, String oTitle, String oMemo,
                         @Nullable byte[] pPhoto, int oDone) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ODATE, oDate);
        values.put(DBHelper.COLUMN_OSTART, oStart);
        values.put(DBHelper.COLUMN_OEND, oEnd);
        values.put(DBHelper.COLUMN_OTITLE, oTitle);
        values.put(DBHelper.COLUMN_OMEMO, oMemo);
        values.put(DBHelper.COLUMN_ODONE, oDone);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ONEUL, null, values);
        db.close();

//        fixme 작성 페이지 사진 기능 완료되면 최적화
        if (pPhoto != null) {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ODONE + " = 1",
                    null, null, null, null);

            if (cursor.moveToLast()) {
                int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));

                cursor.close();
                db.close();

                addPhoto(oNo, pPhoto);
            }
        }
    }

    //    기록중인 일과 불러오기
    public Oneul getStartOneul() {
        Oneul oneul = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ODONE + " = 0",
                null, null, null, null);

        while (cursor.moveToNext()) {
            int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));
            String oDate = cursor.getString(cursor.getColumnIndex((COLUMN_ODATE)));
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oMemo = cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO));

            oneul = new Oneul(oNo, oDate, oStart, null, oTitle, oMemo, null);
        }

        cursor.close();
        db.close();

        return oneul;
    }

    //    일과 사진 가져오기
    public List<Bitmap> getPhoto(int oNo) {
        List<Bitmap> bitmaps = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        while (cursor.moveToNext()) {
            bitmaps.add(BitmapChanger.bytesToBitmap(cursor.getBlob(cursor.getColumnIndex(COLUMN_PPHOTO))));
        }

        cursor.close();
        db.close();

        return bitmaps;
    }

    //    일과 사진 번호 가져오기
    public List<Integer> getpNos(int oNo) {
        List<Integer> integers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PNO}, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        while (cursor.moveToNext()) {
            integers.add(cursor.getInt(cursor.getColumnIndex(COLUMN_PNO)));
        }

        cursor.close();
        db.close();

        return integers;
    }

    public int getPhotoCount(int oNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        int photoCount = cursor.getCount() - 1;

        cursor.close();
        db.close();

        return photoCount;
    }

    //    일과 사진 추가
    public void addPhoto(int oNo, byte[] pPhoto) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ONO, oNo);
        values.put(DBHelper.COLUMN_PPHOTO, pPhoto);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PHOTO, null, values);
        db.close();
    }

    //    일과 사진 삭제
    public void deletePhoto(int pNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_PNO + " = " + pNo, null);
        db.close();
    }

    //    일과 메모 수정
    public void editMemo(int oNo, String oMemo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_OMEMO, oMemo);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
        db.close();
    }

    //    일과 기록 종료 및 저장
    public void endOneul(int oNo, String oEnd) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_OEND, oEnd);
        values.put(DBHelper.COLUMN_ODONE, 1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
        db.close();
    }

    //   수정할 일과 불러오기
    public String[] getEditOneul(int oNo) {
        String[] strings = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ONO + " = " + oNo,
                null, null, null, null);

        while (cursor.moveToNext()) {
            String oDate = cursor.getString(cursor.getColumnIndex((COLUMN_ODATE)));
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oEnd = cursor.getString(cursor.getColumnIndex(COLUMN_OEND));
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oMemo = cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO));

            strings = new String[]{String.valueOf(oNo), oDate, oStart, oEnd, oTitle, oMemo};
        }

        cursor.close();
        db.close();

        return strings;
    }

    //    완료일과 불러오기
    public void getOneul(String oDate, RecyclerView r_oneul, OneulAdapter adapter, String sort) {
        adapter.clear();
        sort = " " + sort;
        Oneul oneul;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_ODATE + " = ? AND " + COLUMN_ODONE + " = 1",
                new String[]{oDate}, null, null, COLUMN_OSTART + sort + ", " + COLUMN_ONO + sort);

        while (cursor.moveToNext()) {
            int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oEnd = cursor.getString(cursor.getColumnIndex(COLUMN_OEND));
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oMemo = cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO));
            byte[] pPhoto = null;

            Cursor photoCursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                    null, null, null, COLUMN_PNO + " ASC");

            if (photoCursor.moveToFirst()) {
                pPhoto = photoCursor.getBlob(photoCursor.getColumnIndex(COLUMN_PPHOTO));
            }

            photoCursor.close();

            oneul = new Oneul(oNo, oDate, oStart, oEnd, oTitle, oMemo, pPhoto);
            adapter.addItem(oneul);
        }

        cursor.close();
        db.close();

        r_oneul.setAdapter(adapter);
    }

    //    일과 날짜 불러오기
    public List<CalendarDay> getOneulDates() {
        ArrayList<CalendarDay> calendarDays = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ONEUL, new String[]{COLUMN_ODATE}, COLUMN_ODONE + " = 1",
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String oDate = cursor.getString(cursor.getColumnIndex(COLUMN_ODATE));
            calendarDays.add(CalendarDay.from(LocalDate.parse(oDate)));
        }

        cursor.close();
        db.close();

        return calendarDays;
    }

    //    일과 삭제
    public void deleteOneul(int oNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_ONO + " = " + oNo, null);
        db.delete(TABLE_ONEUL, COLUMN_ONO + " = " + oNo, null);
        db.close();
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
        db.close();
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