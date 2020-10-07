package com.oneul.oneul;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.WriteActivity;
import com.oneul.extra.BitmapChanger;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.fragment.HomeFragment;

public class OneulHolder extends RecyclerView.ViewHolder {
    TextView t_oNo, t_oTitle, t_oTime, t_oMemo, t_oMore, t_oPhotoCount;
    ImageView i_oPhoto;
    RelativeLayout rl_oPhoto;

    Context context;
    DBHelper dbHelper;

    int oNo;

    public OneulHolder(final View itemView) {
        super(itemView);

        t_oNo = itemView.findViewById(R.id.t_oNo);
        t_oTime = itemView.findViewById(R.id.t_oTime);
        t_oTitle = itemView.findViewById(R.id.t_oTitle);
        t_oMemo = itemView.findViewById(R.id.t_oMemo);
        t_oMore = itemView.findViewById(R.id.t_oMore);
        i_oPhoto = itemView.findViewById(R.id.i_oPhoto);
        t_oPhotoCount = itemView.findViewById(R.id.t_oPhotoCount);
        rl_oPhoto = itemView.findViewById(R.id.rl_oPhoto);

        context = itemView.getContext();
        dbHelper = DBHelper.getDB(context);

        oNo = Integer.parseInt(t_oNo.getText().toString());

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"수정", "삭제"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
//                            수정
                                    case 0:
                                        Intent intent = new Intent(context, WriteActivity.class);
                                        intent.putExtra("editOneul", dbHelper.getEditOneul(oNo));
                                        context.startActivity(intent);
                                        break;
//                            삭제
                                    case 1:
                                        dbHelper.deleteOneul(oNo);
                                        //        오늘이면
                                        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
                                            dbHelper.getOneul(MainActivity.showDay, HomeFragment.r_oneul,
                                                    HomeFragment.adapter, "DESC");

//                                            오늘이 아니면
                                        } else {
                                            dbHelper.getOneul(MainActivity.showDay, HomeFragment.r_oneul,
                                                    HomeFragment.adapter, "ASC");
                                        }
                                        break;
                                }
                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });

        rl_oPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                fixme 사진뷰어 추가
//                if (viewer.getParent() != null) {
//                    ((ViewGroup) viewer.getParent()).removeView(viewer);
//                }
            }
        });

        rl_oPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemView.performLongClick();

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
        if (oneul.pPhoto != null) {
            i_oPhoto.setImageBitmap(BitmapChanger.bytesToBitmap(oneul.pPhoto));
            t_oPhotoCount.setVisibility(View.GONE);

            if (dbHelper.getPhotoCount(oneul.oNo) > 0) {
                String photoCount = "+" + dbHelper.getPhotoCount(oneul.oNo);
                t_oPhotoCount.setText(photoCount);
                t_oPhotoCount.setVisibility(View.VISIBLE);
            }
        } else {
            rl_oPhoto.setVisibility(View.GONE);
        }
    }
}
//fixme 최적화