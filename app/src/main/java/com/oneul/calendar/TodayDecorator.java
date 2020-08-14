package com.oneul.calendar;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;

import com.oneul.extra.DateTime;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.LocalDate;

public class TodayDecorator implements DayViewDecorator {
    public TodayDecorator() {
    }

    @Override
    public boolean shouldDecorate(final CalendarDay day) {
        final LocalDate localDate = day.getDate();

        return localDate.equals(LocalDate.parse(DateTime.today()));
    }

    @Override
    public void decorate(final DayViewFacade view) {
        view.addSpan(new BackgroundColorSpan(Color.parseColor("#E88346")));
    }
}