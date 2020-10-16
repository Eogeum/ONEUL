package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.oneul.R;
import com.oneul.extra.DBHelper;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.File;
import java.text.SimpleDateFormat;

public class DialogFragment {
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 202;

    public static String photoPath;
    private static AlertDialog dialog;

    public static void checkMemoDialog(final Activity activity, final int bottomButtonId) {
        dialog = new AlertDialog.Builder(activity)
                .setMessage("메모 작성을 취소합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.findViewById(R.id.ll_cancelMemo).callOnClick();

                        if (bottomButtonId != 0) {
                            activity.findViewById(bottomButtonId).callOnClick();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
            }
        });
        dialog.show();
    }

//    fixme 홀더랑 같이 수정해야함 삭제 안됨
//    public static StfalconImageViewer<Bitmap> imageViewerDialog(final Context context,
//                                                                final StfalconImageViewer<Bitmap> viewer,
//                                                                final int oNo) {
//        final DBHelper dbHelper = DBHelper.getDB(context);
//
//        StfalconImageViewer.Builder<Bitmap> builder = new StfalconImageViewer
//                .Builder<>(context, dbHelper.getPhotos(oNo), new ImageLoader<Bitmap>() {
//            @Override
//            public void loadImage(ImageView imageView, Bitmap image) {
//                imageView.setImageBitmap(image);
//            }
//        });
//        final View overlay = DialogFragment.newOverlay(context, viewer, oNo);
//
//        return builder
//                .withOverlayView(overlay)
//                .withImageChangeListener(new OnImageChangeListener() {
//                    @Override
//                    public void onImageChange(final int position) {
//                        overlay.findViewById(R.id.ll_deletePhoto).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                DialogFragment.deletePhotoDialog(context, viewer, oNo,
//                                        dbHelper.getpNos(oNo).get(position));
//                            }
//                        });
//
//                        overlay.findViewById(R.id.ll_downloadPhoto).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                File file = DialogFragment.getFile("/Download");
//                                try {
//                                    FileOutputStream stream = new FileOutputStream(file);
//                                    stream.write(dbHelper.getPhoto(dbHelper.getpNos(oNo).get(position)));
//                                    stream.flush();
//                                    stream.close();
//
//                                    Toast.makeText(context, "\"/Download\"에 저장했습니다.",
//                                            Toast.LENGTH_SHORT).show();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                })
//                .withDismissListener(new OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        dbHelper.refreshRecyclerView();
//                    }
//                })
//                .build();
//    }
//
//    public static View newOverlay(final Context context, final StfalconImageViewer<Bitmap> view, final int oNo) {
//        final DBHelper dbHelper = DBHelper.getDB(context);
//
//        View overlay = View.inflate(context, R.layout.view_overlay, null);
//        LinearLayout ll_deletePhoto = overlay.findViewById(R.id.ll_deletePhoto);
//        ll_deletePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment.deletePhotoDialog(context, view, oNo, dbHelper.getpNos(oNo).get(0));
//            }
//        });
//
//        LinearLayout ll_downloadPhoto = overlay.findViewById(R.id.ll_downloadPhoto);
//        ll_downloadPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //        todo 효율적인 퍼미션 체크로 변경
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
//                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
//                    try {
//                        FileOutputStream stream = new FileOutputStream(DialogFragment.getFile("/Download"));
//                        stream.write(dbHelper.getPhoto(dbHelper.getpNos(oNo).get(0)));
//                        stream.flush();
//                        stream.close();
//
//                        Toast.makeText(context, "\"/Download\"에 저장했습니다.", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }, 1);
//                }
//            }
//        });
//
//        return overlay;
//    }

    public static void addPhotoDialog(final Activity activity) {
        dialog = new AlertDialog.Builder(activity)
                .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
//                            카메라 선택 시
                            case 0:
//                                임시 파일 만들기
                                File file = getFile("/DCIM");
                                photoPath = file.getAbsolutePath();

//                                카메라 인텐트
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                                    Uri photoURI = FileProvider.getUriForFile(activity,
                                            "com.oneul.fileprovider", file);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                }

                                activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                break;

//                            갤러리 선택 시
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK)
                                        .setType("image/*")
                                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                        .setType(MediaStore.Images.Media.CONTENT_TYPE);

                                activity.startActivityForResult(galleryIntent,
                                        GALLERY_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        dialog.show();
    }

    public static void deletePhotoDialog(final Context context, final StfalconImageViewer<Bitmap> viewer,
                                         final int oNo, final int pNo) {
        dialog = new AlertDialog.Builder(context)
                .setMessage("사진을 삭제합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DBHelper dbHelper = DBHelper.getDB(context);
                        dbHelper.deletePhoto(pNo);
                        viewer.updateImages(dbHelper.getPhotos(oNo));

                        Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
            }
        });
        dialog.show();
    }


    @SuppressLint("SimpleDateFormat")
    public static File getFile(String folder) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(System.currentTimeMillis()) + ".jpg";
        File fileDir = new File(Environment.getExternalStorageDirectory() + folder, "O:NEUL");

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return new File(fileDir, fileName);
    }
}