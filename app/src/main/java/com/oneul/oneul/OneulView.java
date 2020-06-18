package com.oneul.oneul;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oneul.R;

public class OneulView extends LinearLayout {
    TextView oTitle, oTime;

    public OneulView(Context context) {
        super(context);
        init(context);
    }

    public OneulView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_oneul_list, this, true);
        oTime = findViewById(R.id.t_oTime);
        oTitle = findViewById(R.id.t_oTitle);
    }


    public void setoStart(String oStart) {
        this.oTime.setText(oStart);
    }

    public void setoEnd(String oEnd) {
        this.oTime.append(" ~ " + oEnd);
    }

    public void setoTitle(String oTitle) {
        this.oTitle.setText(oTitle);
    }
}
