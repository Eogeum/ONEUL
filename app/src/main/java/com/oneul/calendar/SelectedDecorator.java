package com.oneul.calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDecorator implements DayViewDecorator {
    private CalendarDay calendarDay;

    public SelectedDecorator(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    @Override
    public boolean shouldDecorate(final CalendarDay day) {
        return day == this.calendarDay;
    }

    @Override
    public void decorate(final DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")));
    }
}