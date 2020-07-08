package com.oneul.oneul;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oneul.R;

public class OneulView extends LinearLayout {
    TextView t_oTitle, t_oTime, t_oMemo, t_oMore;

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
        inflater.inflate(R.layout.home_list, this, true);
        t_oTime = findViewById(R.id.t_oTime);
        t_oTitle = findViewById(R.id.t_oTitle);
        t_oMemo = findViewById(R.id.t_oMemo);
        t_oMore = findViewById(R.id.t_oMore);
    }


    public void setoStart(String oStart) {
        t_oTime.setText(oStart);
    }

    public void setoEnd(String oEnd) {
        t_oTime.append(" ~ " + oEnd);
    }

    public void setoTitle(String oTitle) {
        t_oTitle.setText(oTitle);
    }

    public void setoMemo(String oMemo) {
        t_oMemo.setText(oMemo);
    }

}
