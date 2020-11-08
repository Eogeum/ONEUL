package com.oneul.extra;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class DateTime {
    //    현재 날짜 불러오기
    public static String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
    }

    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(System.currentTimeMillis());
    }

    //    현재 시간 불러오기
    public static String nowTime() {
        return new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
    }

    public static long calculateTime(String oStart, String oEnd) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            return (Objects.requireNonNull(format.parse(oEnd)).getTime()
                    - Objects.requireNonNull(format.parse(oStart)).getTime()) / 60000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //    밀리세컨 시간으로 변환
    public static String minuteToTime(long minute) {
        String time;

        if (minute >= 60) {
            time = ((int) minute / 60) + "시간";

            if (minute % 60 > 0) {
                time += " " + minute % 60 + "분";
            }
        } else {
            time = minute + "분";
        }

        return time;
    }

    //    날짜로 자르기
    public static String stringToDay(String string) {
        return string.substring(0, 10);
    }

    //    월별로 자르기
    public static String stringToMonth(String string) {
        return string.substring(5, 10);
    }

    //    시간으로 자르기
    public static String stringToTime(String string) {
        return string.substring(11, 16);
    }
}