package com.oneul.oneul;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.WriteActivity;
import com.oneul.fragment.HomeFragment;

public class OneulHolder extends RecyclerView.ViewHolder {
    TextView t_oNo, t_oTitle, t_oTime, t_oMemo, t_oMore;
    LinearLayout ll_oPhoto;

    public OneulHolder(View itemView) {
        super(itemView);

        t_oNo = itemView.findViewById(R.id.t_oNo);
        t_oTime = itemView.findViewById(R.id.t_oTime);
        t_oTitle = itemView.findViewById(R.id.t_oTitle);
        t_oMemo = itemView.findViewById(R.id.t_oMemo);
        t_oMore = itemView.findViewById(R.id.t_oMore);
        ll_oPhoto = itemView.findViewById(R.id.ll_oPhoto);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = v.getContext();
                final Intent intent = new Intent(context, WriteActivity.class);

                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"수정", "삭제"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
//                            수정
                                    case 0:
                                        String[] strings = HomeFragment.dbHelper.getEditOneul(Integer.parseInt(t_oNo.getText().toString()));
                                        intent.putExtra("editOneul", strings);
                                        context.startActivity(intent);
                                        break;
//                            삭제
                                    case 1:
                                        HomeFragment.dbHelper.deleteOneul(Integer.parseInt(t_oNo.getText().toString()));
                                        HomeFragment.dbHelper.getOneul(MainActivity.showDay, HomeFragment.r_oneul, HomeFragment.adapter);
                                        break;
                                }
                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });
    }

    public void onBind(Oneul oneul) {
        t_oNo.setText(String.valueOf(oneul.oNo));
        t_oTime.setText(oneul.oStart);
        t_oTime.append(" ~ " + oneul.oEnd);
        t_oTitle.setText(oneul.oTitle);
        t_oMemo.setText(oneul.oMemo);
    }
}