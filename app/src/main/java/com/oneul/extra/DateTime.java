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

    //    현재 시 불러오기
    public static int nowHour() {
        return Integer.parseInt(new SimpleDateFormat("HH").format(System.currentTimeMillis()));
    }

    //    현재 분 불러오기
    public static int nowMinute() {
        return Integer.parseInt(new SimpleDateFormat("mm").format(System.currentTimeMillis()));
    }
}
