package com.oneul.oneul;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;

public class OneulHolder extends RecyclerView.ViewHolder {
    TextView t_oTitle, t_oTime, t_oMemo, t_oMore;

    public OneulHolder(View itemView) {
        super(itemView);

        t_oTime = itemView.findViewById(R.id.t_oTime);
        t_oTitle = itemView.findViewById(R.id.t_oTitle);
        t_oMemo = itemView.findViewById(R.id.t_oMemo);
        t_oMore = itemView.findViewById(R.id.t_oMore);
    }

    public void onBind(Oneul oneul) {
        t_oTime.setText(oneul.getoStart());
        t_oTime.append(" ~ " + oneul.getoEnd());
        t_oTitle.setText(oneul.getoTitle());
        t_oMemo.setText(oneul.getoMemo());

    }
}
