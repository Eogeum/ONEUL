package com.oneul.extra;

import android.media.Image;

public class StatItem {

    String title_item;
    String time_item;

    public void Item(String title_item, String time_item) {
        this.title_item = title_item;
        this.time_item = time_item;
    }

    public String getTitle() {
        return title_item;
    }

    public String getTime() {
        return time_item;
    }

}
