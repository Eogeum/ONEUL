package com.oneul.extra;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class DateTime {
    //    오늘 날짜 불러오기
    public static String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
    }

    //    현재 시간 불러오기
    public static String nowTime() {
        return new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
    }
}