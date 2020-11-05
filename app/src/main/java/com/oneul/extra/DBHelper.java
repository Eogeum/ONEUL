package com.oneul.extra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.oneul.MainActivity;
import com.oneul.fragment.HomeFragment;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;
import com.oneul.stat.Stat;
import com.oneul.stat.StatAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //    일과 테이블 정보
    public static final String TABLE_ONEUL = "Oneul";
    public static final String COLUMN_ONO = "oNo";
    public static final String COLUMN_OSTART = "oStart";
    public static final String COLUMN_OEND = "oEnd";
    public static final String COLUMN_OTITLE = "oTitle";
    public static final String COLUMN_OMEMO = "oMemo";
    public static final String COLUMN_ODONE = "oDone";

    //    사진 테이블 정보
    public static final String TABLE_PHOTO = "Photo";
    public static final String COLUMN_PNO = "pNo";
    public static final String COLUMN_PPHOTO = "pPhoto";

    //    카테고리 테이블 정보
    public static final String TABLE_CATEGORY = "Category";
    public static final String COLUMN_CNO = "cNo";
    public static final String COLUMN_CCATEGORY = "cCategory";

    //    디비 정보
    private static final String DATABASE_ONEUL = "OneulDB";
    private static final int DATABASE_VERSION = 1;

    //     테이블 생성
    private static final String CREATE_ONEUL = "CREATE TABLE " + TABLE_ONEUL + "(" +
            COLUMN_ONO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_OSTART + " TEXT, " +
            COLUMN_OEND + " TEXT, " +
            COLUMN_OTITLE + " TEXT NOT NULL, " +
            COLUMN_OMEMO + " TEXT, " +
            COLUMN_ODONE + " INTEGER NOT NULL, " +
            COLUMN_CNO + " INTEGER, " +
            "CONSTRAINT o_c_cNo FOREIGN KEY(" + COLUMN_CNO + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CNO + "));";

    private static final String CREATE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + "(" +
            COLUMN_PNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PPHOTO + " BLOB, " +
            COLUMN_ONO + " INTEGER, " +
            "CONSTRAINT p_o_oNo FOREIGN KEY(" + COLUMN_ONO + ") REFERENCES " + TABLE_ONEUL + "(" + COLUMN_ONO + "));";

    private static final String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "(" +
            COLUMN_CNO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CCATEGORY + " TEXT);";

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

    //    ㄴㄴ 카테고리
//    카테고리 추가
    public void addCategory(String cCategory) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CCATEGORY, cCategory);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CATEGORY, null, values);
    }

    //    카테고리 불러오기
    public void getCategory() {

    }

    //    카테고리 삭제
    public void deleteCategory(int cNo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CNO, (Integer) null);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_CNO + " = " + cNo, null);
        db.delete(TABLE_CATEGORY, COLUMN_CNO + " = " + cNo, null);
    }

    //    카테고리 수정
    public void editCategory() {

    }

    //    ㄴㄴ 일과
    //    일과 시작
    public void startOneul(String oStart, String oEnd, String oTitle, String oMemo,
                           @Nullable List<Bitmap> pPhotos, int oDone) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_OSTART, oStart);
        values.put(COLUMN_OEND, oEnd);
        values.put(COLUMN_OTITLE, oTitle);
        values.put(COLUMN_OMEMO, oMemo);
        values.put(COLUMN_ODONE, oDone);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ONEUL, null, values);

        if (pPhotos != null) {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_ONEUL, new String[]{COLUMN_ONO}, COLUMN_ODONE + " = 1",
                    null, null, null, COLUMN_ONO + " DESC");

            if (cursor.moveToFirst()) {
                for (int i = 0; i < pPhotos.size(); i++) {
                    addPhoto(cursor.getInt(cursor.getColumnIndex(COLUMN_ONO)),
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
                    cursor.getString(cursor.getColumnIndex(COLUMN_OSTART)),
                    null,
                    cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO)),
                    null,
                    -1);
        }

        cursor.close();

        return oneul;
    }

    //    완료 일과 불러오기
    public void getOneul(String showDay, RecyclerView r_oneul, OneulAdapter adapter, String sort) {
        sort = " " + sort;
        adapter.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONEUL, null, COLUMN_OSTART + " LIKE ? AND " + COLUMN_ODONE + " = 1",
                new String[]{showDay + "%"}, null, null, COLUMN_OSTART + sort + ", " + COLUMN_ONO + sort);

        while (cursor.moveToNext()) {
            byte[] pPhoto = null;
            int oNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ONO));

            Cursor photoCursor = db.query(TABLE_PHOTO, new String[]{COLUMN_PPHOTO}, COLUMN_ONO + " = " + oNo,
                    null, null, null, COLUMN_PNO + " ASC");

            if (photoCursor.moveToFirst()) {
                pPhoto = photoCursor.getBlob(photoCursor.getColumnIndex(COLUMN_PPHOTO));
            }

            photoCursor.close();

            String oStart = cursor.getString(cursor.getColumnIndex(COLUMN_OSTART));
            String oEnd = cursor.getString(cursor.getColumnIndex(COLUMN_OEND));

            if (DateTime.stringToMonth(oStart).equals(DateTime.stringToMonth(oEnd))) {
                oEnd = DateTime.stringToTime(oEnd);
            } else {
                oEnd = DateTime.stringToTime(oEnd) + " (" + DateTime.stringToMonth(oEnd) + ")";
            }

            adapter.addItem(
                    new Oneul(oNo,
                            DateTime.stringToTime(oStart),
                            oEnd,
                            cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO)),
                            pPhoto,
                            -1));
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
                    cursor.getString(cursor.getColumnIndex(COLUMN_OSTART)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OEND)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_OMEMO)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CNO))};
        }

        cursor.close();

        return strings;
    }

    //    일과 수정
    public void editOneul(int oNo, String oStart, String oEnd, String oTitle, String oMemo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_OSTART, oStart);
        values.put(COLUMN_OEND, oEnd);
        values.put(COLUMN_OTITLE, oTitle);
        values.put(COLUMN_OMEMO, oMemo);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
    }

    //    일과 기록 종료
    public void endOneul(int oNo, String oEnd) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_OEND, oEnd);
        values.put(COLUMN_ODONE, 1);

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
    public void addPhoto(int oNo, List<Bitmap> pPhotos) {
        for (int i = 0; i < pPhotos.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ONO, oNo);
            values.put(COLUMN_PPHOTO, BitmapRefactor.bitmapToBytes(
                    BitmapRefactor.resizeBitmap(pPhotos.get(i))));

            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_PHOTO, null, values);
        }
    }

    public void editPhoto(int oNo, List<Bitmap> bitmaps) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_ONO + " = " + oNo, null);
        addPhoto(oNo, bitmaps);
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
        values.put(COLUMN_OMEMO, oMemo);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ONEUL, values, COLUMN_ONO + " = " + oNo, null);
    }

    //    ㄴㄴ 캘린더
    //    일과 날짜 불러오기
    public List<CalendarDay> getOneulDates() {
        ArrayList<CalendarDay> calendarDays = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ONEUL, new String[]{COLUMN_OSTART}, COLUMN_ODONE + " = 1",
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            calendarDays.add(CalendarDay.from(LocalDate.parse(DateTime.stringToDay(
                    cursor.getString(cursor.getColumnIndex(COLUMN_OSTART))))));
        }

        cursor.close();

        return calendarDays;
    }

    //    ㄴㄴ 통걔
    //    통계 가져오기
    public void getStat(String day, PieChart chart, RecyclerView r_stat, StatAdapter adapter) {
        adapter.clear();

        ArrayList<PieEntry> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_ONEUL, new String[]{COLUMN_OTITLE},
                COLUMN_OSTART + " LIKE ? AND " + COLUMN_ODONE + " = 1",
                new String[]{day.substring(0, 7) + "%"}, null, null, null, null);

        while (cursor.moveToNext()) {
            String oTitle = cursor.getString(cursor.getColumnIndex(COLUMN_OTITLE));
            long time = 0;

            Cursor timeCursor = db.query(TABLE_ONEUL, new String[]{COLUMN_OSTART, COLUMN_OEND},
                    COLUMN_OTITLE + " = ? AND " + COLUMN_ODONE + " = 1",
                    new String[]{oTitle}, null, null, null);

            while (timeCursor.moveToNext()) {
                time += DateTime.calculateTime(
                        timeCursor.getString(timeCursor.getColumnIndex(COLUMN_OSTART)),
                        timeCursor.getString(timeCursor.getColumnIndex(COLUMN_OEND)));
            }

            timeCursor.close();

            list.add(new PieEntry(time, oTitle));
            adapter.addItem(new Stat(oTitle,
                    time >= 60 ? (((int) time / 60) + "시간 " + ((int) time % 60) + "분") : time + "분"));
        }

        cursor.close();

        PieDataSet dataSet = new PieDataSet(list, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value >= 60) {
                    return ((int) value / 60) + "시간 " + ((int) value % 60) + "분";
                } else {
                    return value + "분";
                }
            }
        });

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10);

        chart.setData(data);
        chart.invalidate();
        r_stat.setAdapter(adapter);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONEUL);
        db.execSQL(CREATE_ONEUL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL(CREATE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL(CREATE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONEUL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        this.onCreate(db);
    }
}