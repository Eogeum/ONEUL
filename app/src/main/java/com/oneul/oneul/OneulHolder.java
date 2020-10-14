package com.oneul.oneul;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.oneul.R;
import com.oneul.WriteActivity;
import com.oneul.extra.BitmapRefactor;
import com.oneul.extra.DBHelper;
import com.oneul.fragment.DialogFragment;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnDismissListener;
import com.stfalcon.imageviewer.listeners.OnImageChangeListener;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OneulHolder extends RecyclerView.ViewHolder {
    TextView t_oNo, t_oTitle, t_oTime, t_oMemo, t_oMore, t_oPhotoCount;
    ImageView i_oPhoto;
    RelativeLayout rl_oPhoto;
    LinearLayout ll_deletePhoto, ll_downloadPhoto;
    StfalconImageViewer<Bitmap> viewer;

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

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                oNo = Integer.parseInt(t_oNo.getText().toString());

                new AlertDialog.Builder(context)
                        .setItems(new CharSequence[]{"수정", "삭제"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
//                            수정
                                    case 0:
                                        Intent intent = new Intent(context, WriteActivity.class);
                                        intent.putExtra("editOneul", dbHelper.getOneul(oNo));
                                        context.startActivity(intent);
                                        break;
//                            삭제
                                    case 1:
                                        dbHelper.deleteOneul(oNo);
                                        dbHelper.refreshRecyclerView();
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
                oNo = Integer.parseInt(t_oNo.getText().toString());

                StfalconImageViewer.Builder<Bitmap> builder = new StfalconImageViewer
                        .Builder<>(context, dbHelper.getPhotos(oNo), new ImageLoader<Bitmap>() {
                    @Override
                    public void loadImage(ImageView imageView, Bitmap image) {
                        imageView.setImageBitmap(image);
                    }
                });

                View overlay = View.inflate(context, R.layout.view_overlay, null);
                ll_deletePhoto = overlay.findViewById(R.id.ll_deletePhoto);
                ll_deletePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment.deletePhotoDialog(context, viewer, oNo, dbHelper.getpNos(oNo).get(0));
                    }
                });

                ll_downloadPhoto = overlay.findViewById(R.id.ll_downloadPhoto);
                ll_downloadPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //        todo 효율적인 퍼미션 체크로 변경
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                            FileOutputStream stream = null;
                            try {
                                stream = new FileOutputStream(DialogFragment.getFile("/Download"));
                                stream.write(dbHelper.getPhoto(dbHelper.getpNos(oNo).get(0)));
                                stream.flush();
                                stream.close();

                                Toast.makeText(context, "\"/Download\"에 저장했습니다.", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        }
                    }
                });

                viewer = builder.withOverlayView(overlay)
                        .withImageChangeListener(new OnImageChangeListener() {
                            @Override
                            public void onImageChange(final int position) {
                                ll_deletePhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogFragment.deletePhotoDialog(context, viewer, oNo,
                                                dbHelper.getpNos(oNo).get(position));
                                    }
                                });

                                ll_downloadPhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        File file = DialogFragment.getFile("/Download");
                                        try {
                                            FileOutputStream stream = new FileOutputStream(file);
                                            stream.write(dbHelper.getPhoto(dbHelper.getpNos(oNo).get(position)));
                                            stream.flush();
                                            stream.close();

                                            Toast.makeText(context, "\"/Download\"에 저장했습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        })
                        .withDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                dbHelper.refreshRecyclerView();
                            }
                        })
                        .build();
                viewer.show();
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
            i_oPhoto.setImageBitmap(BitmapRefactor.bytesToBitmap(oneul.pPhoto));
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