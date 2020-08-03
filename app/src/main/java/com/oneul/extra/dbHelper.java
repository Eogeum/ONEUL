package com.oneul.extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.recyclerview.widget.RecyclerView;

import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;

public class dbHelper extends SQLiteOpenHelper {
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
    private static final String CREATE_ONEUL = "CREATE TABLE " + TABLE_ONEUL + "("
            + COLUMN_ONO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ODATE + " TEXT NOT NULL, "
            + COLUMN_OSTART + " TEXT, " + COLUMN_OEND + " TEXT, " + COLUMN_OTITLE + " TEXT NOT NULL, "
            + COLUMN_OMEMO + " TEXT, " + COLUMN_ODONE + " INTEGER NOT NULL);";

    private static final String CREATE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + "("
            + COLUMN_PNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PPHOTO + " BLOB, "
            + COLUMN_ONO + " INTEGER, CONSTRAINT p_o_oNo FOREIGN KEY(" + COLUMN_ONO + ") REFERENCES "
            + TABLE_ONEUL + "(" + COLUMN_ONO + "));";

    //    디비 생성자
    public dbHelper(Context context) {
        super(context, DATABASE_ONEUL, null, DATABASE_VERSION);
    }

    //    기록중인 일과 불러오기
    public Oneul getStartOneul() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ONEUL, null, "oDone = 0", null,
                null, null, null);
        Oneul oneul = null;

        while (cursor.moveToNext()) {
            int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));
            String oDate = cursor.getString(cursor.getColumnIndex((COLUMN_ODATE)));
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oMemo = cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO));

            oneul = new Oneul(oNo, oDate, oStart, oTitle, oMemo);
        }

        cursor.close();
        db.close();

        return oneul;
    }

    //    일과 메모 수정
    public void editMemo(int oNo, String oMemo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_OMEMO, oMemo);

        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
        db.close();
    }

    //    일과 기록 종료, 로우 수정
    public void endOneul(int oNo, String oEnd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_OEND, oEnd);
        values.put(dbHelper.COLUMN_ODONE, 1);

        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
        db.close();
    }

    //    일과 추가
    public void addOneul(String oDate, String oStart, String oEnd, String oTitle, String oMemo, int oDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_ODATE, oDate);
        values.put(dbHelper.COLUMN_OSTART, oStart);
        values.put(dbHelper.COLUMN_OEND, oEnd);
        values.put(dbHelper.COLUMN_OTITLE, oTitle);
        values.put(dbHelper.COLUMN_OMEMO, oMemo);
        values.put(dbHelper.COLUMN_ODONE, oDone);

        db.insert(TABLE_ONEUL, null, values);
        db.close();
    }

    //    일과 불러오기
    public void getOneul(String oDate, RecyclerView r_oneul, OneulAdapter adapter) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, "oDate = ? AND oDone = 1",
                new String[]{oDate}, null, null, "oStart DESC, oNo DESC");
        Oneul oneul;

        adapter.clear();

        while (cursor.moveToNext()) {
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oEnd = cursor.getString(cursor.getColumnIndex(COLUMN_OEND));
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oMemo = cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO));
            oneul = new Oneul(oDate, oStart, oEnd, oTitle, oMemo);

            adapter.addItem(oneul);
            r_oneul.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
        cursor.close();
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
