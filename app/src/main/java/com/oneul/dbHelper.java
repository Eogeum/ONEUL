package com.oneul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class dbHelper extends SQLiteOpenHelper {

    //    테이블 정보
    public static final String TABLE_NAME = "Oneul";
    public static final String COLUMN_ONO = "oNo";
    public static final String COLUMN_OTITLE = "oTitle";
    public static final String COLUMN_OSTART = "oStart";
    public static final String COLUMN_OEND = "oEnd";

    //    디비 정보
    private static final String DATABASE_NAME = "OneulDB";
    private static final int DATABASE_VERSION = 1;

    //     테이블 생성
    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "(" + COLUMN_ONO + " integer PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_OTITLE + " text, " + COLUMN_OSTART + " text, " + COLUMN_OEND + " text);";

    //    디비 생성자
    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //    일과 추가
    public void addOneul(String oTitle, String oStart, String oEnd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_OTITLE, oTitle);
        values.put(dbHelper.COLUMN_OSTART, oStart);
        values.put(dbHelper.COLUMN_OEND, oEnd);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //    일과 불러오기
    public String[] getOneul(int oNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "oNo = ?", new String[]{Integer.toString(oNo)},
                null, null, null);
        String[] oneul = null;

        while (cursor.moveToNext()) {
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oEnd = cursor.getString(cursor.getColumnIndex(COLUMN_OEND));
            oneul = new String[]{oTitle, oStart, oEnd};
        }


        return oneul;
    }

    //     테이블 행 추가
//    private static final String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
//            + TABLE_TEAM + " ADD COLUMN " + COLUMN_COACH + " string;";
//
//    private static final String DATABASE_ALTER_TEAM_2 = "ALTER TABLE "
//            + TABLE_TEAM + " ADD COLUMN " + COLUMN_STADIUM + " string;";

    // 앱을 삭제후 앱을 재설치하면 기존 DB파일은 앱 삭제시 지워지지 않기 때문에
    // 테이블이 이미 있다고 생성 에러남
    // 앱을 재설치시 데이터베이스를 삭제해줘야함.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);

        // 테이블 행 추가
//        if (oldVersion < 2) {
//            db.execSQL(DATABASE_ALTER_1);
//        }
//        if (oldVersion < 3) {
//            db.execSQL(DATABASE_ALTER_2);
//        }
    }


}
