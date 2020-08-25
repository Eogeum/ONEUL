package com.oneul;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.extra.DateTime;
import com.oneul.fragment.DialogFragment;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.StatFragment;

public class MainActivity extends AppCompatActivity {
    //    ㄴㄴ 리퀘스트 코드
    final int CAMERA_REQUEST_CODE = 101;
    final int GALLERY_REQUEST_CODE = 202;

    //    ㄴㄴ 데이터 저장
    public static String showDay = DateTime.today();
    public static boolean useEditMemo = false;

    //    ㄴㄴ 하단 메뉴
    BottomNavigationView bot_menu;

    //    뒤로가기 종료
    boolean doubleBackToExitPressedOnce = false;

    //    에딧텍스트 언포커스
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ㄴㄴ 상단
        createNotificationChannel();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
//        RemoteViews fixedNotice = new RemoteViews(getPackageName(), R.layout.test);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "fixed")
                .setSmallIcon(R.drawable.ic_home1)
                .setContentTitle("O:NEUL")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCustomContentView(fixedNotice)
                .setOngoing(true)
                .setColor(Color.parseColor("#E88346"))
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, builder.build());


//        시작 시 홈화면 불러오기
        openFragment(HomeFragment.newInstance());

//        하단 메뉴 클릭 시
        bot_menu = findViewById(R.id.bot_menu);
        bot_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                메모 작성중일 시
                if (useEditMemo) {
                    DialogFragment.editMemoDialog(MainActivity.this, item.getItemId());

                    return false;
                } else {
//                선택한 화면으로 전환
                    switch (item.getItemId()) {
                        case R.id.bot_menu_home:
                            openFragment(HomeFragment.newInstance());

                            return true;

                        case R.id.bot_menu_write:
                            openFragment(StatFragment.newInstance());

                            return true;

                        case R.id.bot_menu_setting:
                            openFragment(SettingFragment.newInstance(""));

                            return true;
                    }
                }

                return false;
            }
        });
    }

    //    뒤로가기 종료
    @Override
    public void onBackPressed() {
//        메모 작성중일 시
        if (useEditMemo) {
            DialogFragment.editMemoDialog(this, 0);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "종료하려면 한 번 더 누르세요.", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    //    화면 전환
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragmentId = getSupportFragmentManager().findFragmentById(R.id.container);
//        같은 탭을 누를 시 쇼데이 초기화
        if (fragmentId != null) {
            if (fragment.getClass() == fragmentId.getClass()) {
                showDay = DateTime.today();
            }
        }

        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:

                    break;

                case GALLERY_REQUEST_CODE:


            }
        }
    }

    private void createNotificationChannel() {
//        오레오 이상이면
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fixed", "고정", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

//todo 슬라드 날짜 변경
//todo 사진 저장 취소 아이콘
// fixme : 화면에 날짜를 표시할 방법
// fixme : 1일날 시작한 일과를 2일에 완료하면 화면에 일과 시간을 어떻게 표시할 지