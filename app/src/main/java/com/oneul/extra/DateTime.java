package com.oneul.extra;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

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

    public static long calculateTime(String day, String startTime, String endTime) {
        try {
            long startDate = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(day + " " + startTime))
                    .getTime();

            long endDate = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(day + " " + endTime))
                    .getTime();

            if (Integer.parseInt(startTime.replace(":", ""))
                    > Integer.parseInt(endTime.replace(":", ""))) {
                endDate += 86400000;
            }

            return ((endDate - startDate) / 3600000 < 0) ? 0 : (endDate - startDate) / 3600000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}