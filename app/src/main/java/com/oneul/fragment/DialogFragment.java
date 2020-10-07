package com.oneul.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.oneul.R;

import java.io.File;
import java.text.SimpleDateFormat;

public class DialogFragment {
    public static String photoPath;
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 202;

    private static AlertDialog dialog;

    public static void editMemoDialog(final Activity activity, final int bottomButtonId) {
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

    public static AlertDialog calendarDialog(Activity activity, View view) {
        dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();

        return dialog;
    }

    public static void checkPermissionDialog(final Activity activity) {
//        권한 없다면
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != 0 ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            dialog = new AlertDialog.Builder(activity)
                    .setMessage("사진 첨부를 위해 카메라 및\n저장소 권한이 필요합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, 1);
                        }
                    })
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E88346"));
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    activity.finish();
                }
            });
            dialog.show();

//            권한 있다면
        } else {
            addPhotoDialog(activity);
        }
    }

    public static void addPhotoDialog(final Activity activity) {
        dialog = new AlertDialog.Builder(activity)
                .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
//                            카메라 선택 시
                            case 0:
//                                임시 파일 만들기
                                String fileName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
                                        .format(System.currentTimeMillis()) + ".jpg";
                                File fileDir = new File(Environment.getExternalStorageDirectory() +
                                        "/DCIM", "O:NEUL");
                                if (!fileDir.exists()) {
                                    fileDir.mkdirs();
                                }
                                File file = new File(fileDir, fileName);
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

                                activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
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

//    fixme 사진 수정 기능
//    public static StfalconImageViewer<Bitmap> imageViewer(Context context, final DBHelper dbHelper) {
//        View viewer = View.inflate(context, R.layout.view_overlay, null);
//        ImageView i_delete = viewer.findViewById(R.id.i_delete);
//        i_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dbHelper.deletePhoto(integers.get(0));
//            }
//        });
//
//        List<Integer> integers;
//        List<Bitmap> bitmaps;
//        integers = dbHelper.getpNos(oNo);
//
//        StfalconImageViewer<Bitmap> imageViewer = new StfalconImageViewer.Builder<>(context, bitmaps, new ImageLoader<Bitmap>() {
//            @Override
//            public void loadImage(ImageView imageView, Bitmap image) {
//                imageView.setImageBitmap(image);
//            }
//        })
//                .withOverlayView(viewer)
//                .withImageChangeListener(new OnImageChangeListener() {
//                    @Override
//                    public void onImageChange(final int position) {
//                        i_delete.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//
//                        });
//                    }
//                })
//                .show();
//
//        AlertDialog dialog = new AlertDialog.Builder(context)
//                .setMessage("사진을 삭제합니다.")
//                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dbHelper.deletePhoto(integers.get(position));
//                        imageViewer.updateImages();
//                        Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                })
//                .create();
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#E88346"));
//            }
//        });
//        dialog.show();
//
//        return null;
//    }
}

//fixme 최적화