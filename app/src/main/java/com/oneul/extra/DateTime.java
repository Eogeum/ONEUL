package com.oneul.extra;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateTime {
    //    오늘 날짜 불러오기
    public static String today() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/M/d");

        return dateFormat.format(now);
    }

    //    현재 시간 불러오기
    public static String nowTime() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        return timeFormat.format(now);
    }

    //    현재 시 불러오기
    public static int nowHour() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");

        return Integer.parseInt(hourFormat.format(now));
    }

    //    현재 분 불러오기
    public static int nowMinute() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");

        return Integer.parseInt(minuteFormat.format(now));
    }
}
