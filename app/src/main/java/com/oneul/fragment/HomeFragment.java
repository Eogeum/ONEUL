package com.oneul.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oneul.CameraActivity;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.WriteActivity;
import com.oneul.extra.Animation;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.oneul.Oneul;
import com.oneul.oneul.OneulAdapter;
import com.oneul.service.RealService;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnDismissListener;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.Objects;

public class HomeFragment extends Fragment {
    //    ㄴㄴ 리사이클
    public static RecyclerView r_oneul;
    public static OneulAdapter adapter = new OneulAdapter();

    //    ㄴㄴ 뷰
    //    투데이박스
    LinearLayout ll_todayBox;
    EditText et_oTitle;
    //    스타트박스
    FrameLayout fl_startBox;
    ImageView i_memoBox;
    TextView t_oTitle, t_oTime;
    //    스타트박스 메모
    LinearLayout ll_memoBox, ll_picMemo, ll_saveMemo, ll_cancelMemo;
    EditText et_oMemo;
    //    기타
    TextView t_oDate;

    //    ㄴㄴ 기타
    DBHelper dbHelper;
    InputMethodManager imm;
    AlertDialog calendarDialog;

    //    ㄴㄴ 플로팅 버튼
    FloatingActionButton fab_goWrite;

    //    ㄴㄴ 이미지뷰어
    HorizontalScrollView hs_imagePreview;
    LinearLayout ll_imagePreview;
    StfalconImageViewer<Bitmap> viewer;
    LinearLayout ll_deletePhoto, ll_downloadPhoto;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);

//        ㄴㄴ 리사이클
        r_oneul = homeView.findViewById(R.id.r_oneul);
        r_oneul.setLayoutManager(new LinearLayoutManager(getActivity()));
        r_oneul.setAdapter(adapter);
        r_oneul.setHasFixedSize(true);
        r_oneul.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    fab_goWrite.show();
                } else if (dy > 0) {
                    fab_goWrite.hide();
                }
            }
        });

//        ㄴㄴ 뷰
//        투데이박스
        ll_todayBox = homeView.findViewById(R.id.ll_todayBox);
        et_oTitle = homeView.findViewById(R.id.et_oTitle);
//        스타트박스
        fl_startBox = homeView.findViewById(R.id.fl_startBox);
        i_memoBox = homeView.findViewById(R.id.i_memoBox);
        t_oTitle = homeView.findViewById(R.id.t_oTitle);
        t_oTime = homeView.findViewById(R.id.t_oTime);
//        스타트박스 메모
        ll_memoBox = homeView.findViewById(R.id.ll_memoBox);
//        기타
        t_oDate = homeView.findViewById(R.id.t_oDate);
        ll_imagePreview = homeView.findViewById(R.id.ll_imagePreview);
        hs_imagePreview = homeView.findViewById(R.id.hs_imagePreview);

//        ㄴㄴ 기타
        dbHelper = DBHelper.getDB(getActivity());
        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

        calendarDialog = DialogFragment.calendarDialog(getContext(), t_oDate);
        calendarDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dateChange();
            }
        });
        homeView.findViewById(R.id.ll_goCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.useEditMemo) {
                    DialogFragment.checkMemoDialog(getActivity(), 0);
                } else {
                    calendarDialog.show();
                }
            }
        });

//        ㄴㄴ 플로팅 버튼
        fab_goWrite = homeView.findViewById(R.id.fab_goWrite);
        fab_goWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WriteActivity.class));
            }
        });

//        투데이박스 입력 완료
        homeView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.getStartOneul() == null) {
//                    일과 제목이 없으면
                    if (TextUtils.isEmpty(et_oTitle.getText().toString().trim())) {
                        Toast.makeText(getActivity(), "일과 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();

//                        투데이박스 포커스, 키보드 올리기
                        et_oTitle.requestFocus();
                        imm.showSoftInput(et_oTitle, InputMethodManager.SHOW_IMPLICIT);
                    } else {
//                        기록중인 일과가 없으면 기록 시작
                        if (dbHelper.getStartOneul() == null) {
                            dbHelper.startOneul(DateTime.today(), DateTime.nowTime(), null,
                                    et_oTitle.getText().toString(), null, null, 0);
                        }

//                       새로고침 및 투데이박스 값 초기화
                        dateChange();
                        et_oTitle.getText().clear();
//                       알림 새로고침
                        Objects.requireNonNull(getActivity()).getSystemService(NotificationManager.class)
                                .notify(101, RealService.createNotification(getActivity()));
                    }
                } else {
//                    새로고침 및 투데이박스 값 초기화
                    Toast.makeText(getActivity(), "이미 기록중인 일과가 있습니다.", Toast.LENGTH_SHORT).show();

                    dateChange();
                    et_oTitle.getText().clear();
//                   알림 새로고침
                    Objects.requireNonNull(getActivity()).getSystemService(NotificationManager.class)
                            .notify(101, RealService.createNotification(getActivity()));
                }
            }
        });

//        메모박스 확장 축소
        homeView.findViewById(R.id.cl_startBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ll_memoBox.getVisibility()) {
                    case View.GONE:
                        Animation.expand(ll_memoBox);
                        i_memoBox.setImageResource(R.drawable.ic_callapse);
                        break;

                    case View.VISIBLE:
                        if (MainActivity.useEditMemo) {
                            DialogFragment.checkMemoDialog(getActivity(), 0);
                        } else {
                            Animation.collapse(ll_memoBox);
                            i_memoBox.setImageResource(R.drawable.ic_expand);
                        }
                        break;

                    case View.INVISIBLE:
                        Toast.makeText(getActivity(), "ERROR : RETURN INVISIBLE", Toast.LENGTH_LONG).show();
                        break;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = ll_memoBox.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        ll_memoBox.setLayoutParams(layoutParams);
                    }
                }, 1000);
            }
        });

//        사진메모 작성 활성화
        ll_picMemo = homeView.findViewById(R.id.ll_pictureMemo);
        ll_picMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.getPhotoCount(dbHelper.getStartOneul().getoNo()) + 1 < 5) {
                    startActivity(new Intent(getActivity(), CameraActivity.class));
                } else {
                    Toast.makeText(getActivity(), "최대 5장까지만 추가가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        메모 작성 활성화
        et_oMemo = homeView.findViewById(R.id.et_oMemo);
        et_oMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                에딧 텍스트 포커싱 시
                if (hasFocus) {
                    et_oMemo.setText(dbHelper.getStartOneul().getoMemo());
                    imm.showSoftInput(et_oMemo, InputMethodManager.SHOW_IMPLICIT);
                    ll_picMemo.setVisibility(View.GONE);
                    ll_cancelMemo.setVisibility(View.VISIBLE);
                    ll_saveMemo.setVisibility(View.VISIBLE);
                    MainActivity.useEditMemo = true;
                }
            }
        });

//        메모 작성 취소
        ll_cancelMemo = homeView.findViewById(R.id.ll_cancelMemo);
        ll_cancelMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_picMemo.setVisibility(View.VISIBLE);
                ll_cancelMemo.setVisibility(View.GONE);
                ll_saveMemo.setVisibility(View.GONE);
                MainActivity.useEditMemo = false;

//                새로고침
                et_oMemo.setText(dbHelper.getStartOneul().getoMemo());
            }
        });

//        작성 메모 저장
        ll_saveMemo = homeView.findViewById(R.id.ll_saveMemo);
        ll_saveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_picMemo.setVisibility(View.VISIBLE);
                ll_cancelMemo.setVisibility(View.GONE);
                ll_saveMemo.setVisibility(View.GONE);
                MainActivity.useEditMemo = false;

//                메모 저장 및 새로고침
                dbHelper.editMemo(dbHelper.getStartOneul().getoNo(), et_oMemo.getText().toString());
                Toast.makeText(getActivity(), "메모를 저장했습니다.", Toast.LENGTH_LONG).show();
            }
        });

//        스타트박스 기록 완료
        homeView.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.getStartOneul() != null) {
//                    메모 작성중일 시
                    if (MainActivity.useEditMemo) {
                        DialogFragment.checkMemoDialog(getActivity(), 0);
                    } else {
//                        쇼데이 변경
                        MainActivity.showDay = dbHelper.getStartOneul().getoDate();

//                        기록 종료 및 새로고침
                        dbHelper.endOneul(dbHelper.getStartOneul().getoNo(), DateTime.nowTime());
                        Toast.makeText(getActivity(), MainActivity.showDay + "\n일과를 저장했습니다.",
                                Toast.LENGTH_LONG).show();
                        dateChange();

//                        메모박스 초기화
                        i_memoBox.setImageResource(R.drawable.ic_expand);
                        Animation.collapse(ll_memoBox);
//                        알림 새로고침
                        Objects.requireNonNull(getActivity()).getSystemService(NotificationManager.class)
                                .notify(101, RealService.createNotification(getActivity()));
                    }
                } else {
                    i_memoBox.setImageResource(R.drawable.ic_expand);
                    Animation.collapse(ll_memoBox);
                    dateChange();
//                   알림 새로고침
                    Objects.requireNonNull(getActivity()).getSystemService(NotificationManager.class)
                            .notify(101, RealService.createNotification(getActivity()));
                }
            }
        });

        return homeView;
    }

    //    날짜 확인 및 헤더 변경
    public void dateChange() {
//        오늘이면
        if (TextUtils.equals(MainActivity.showDay, DateTime.today())) {
//            기록중인 일과 있으면
            if (dbHelper.getStartOneul() != null) {
//                스타트박스 표시
                ll_todayBox.setVisibility(View.GONE);
                fl_startBox.setVisibility(View.VISIBLE);

//                디비에서 기록중인 일과 불러오기
                Oneul startOneul = dbHelper.getStartOneul();
                t_oTime.setText(startOneul.getoStart());
                t_oTitle.setText(startOneul.getoTitle());
                et_oMemo.setText(startOneul.getoMemo());

//                디비에서 사진 불러오기
                final int oNo = startOneul.getoNo();
                int photoCount = dbHelper.getPhotoCount(oNo) + 1;

                if (photoCount > 0) {
                    hs_imagePreview.setVisibility(View.VISIBLE);
                    ll_imagePreview.removeAllViews();

                    for (int i = 0; i < photoCount; i++) {
                        final ImageView imageView = new ImageView(getActivity());
                        imageView.setImageBitmap(dbHelper.getPhotos(oNo).get(i));
                        imageView.setAdjustViewBounds(true);
                        imageView.setPadding(0, 0, 16, 0);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final View view = View.inflate(getContext(), R.layout.view_overlay, null);

                                ll_deletePhoto = view.findViewById(R.id.ll_deletePhoto);
                                ll_deletePhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogFragment.deletePhotoDialog(getContext(), viewer, oNo,
                                                dbHelper.getpNos(oNo).get(viewer.currentPosition()));

                                        ll_imagePreview.removeViewAt(viewer.currentPosition());
                                    }
                                });

                                ll_downloadPhoto = view.findViewById(R.id.ll_downloadPhoto);
                                ll_downloadPhoto.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogFragment.downloadPhoto(getContext(),
                                                dbHelper.getPhoto(dbHelper.getpNos(oNo).get(viewer.currentPosition())));
                                    }
                                });

                                viewer = new StfalconImageViewer.Builder<>(getContext(), dbHelper.getPhotos(oNo),
                                        new ImageLoader<Bitmap>() {
                                            @Override
                                            public void loadImage(ImageView imageView, Bitmap image) {
                                                imageView.setImageBitmap(image);
                                            }
                                        })
                                        .withStartPosition(ll_imagePreview.indexOfChild(imageView))
                                        .withOverlayView(view)
                                        .withDismissListener(new OnDismissListener() {
                                            @Override
                                            public void onDismiss() {
                                                if (ll_imagePreview.getChildCount() == 0) {
                                                    hs_imagePreview.setVisibility(View.GONE);
                                                }
                                            }
                                        })
                                        .build();

                                viewer.show();
                            }
                        });

                        ll_imagePreview.addView(imageView);
                    }

                    ll_imagePreview.getChildAt(ll_imagePreview.getChildCount() - 1).setPadding(0, 0, 0, 0);
                } else {
                    hs_imagePreview.setVisibility(View.GONE);
                }
            } else {
//                투데이박스 표시
                ll_todayBox.setVisibility(View.VISIBLE);
                fl_startBox.setVisibility(View.GONE);
            }

            //        일과 불러오기
            t_oDate.setText(MainActivity.showDay);
            dbHelper.getOneul(MainActivity.showDay, r_oneul, adapter, "DESC");

//            오늘이 아니면
        } else {
            ll_todayBox.setVisibility(View.GONE);
            fl_startBox.setVisibility(View.GONE);

            //        일과 불러오기
            t_oDate.setText(MainActivity.showDay);
            dbHelper.getOneul(MainActivity.showDay, r_oneul, adapter, "ASC");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        dateChange();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}