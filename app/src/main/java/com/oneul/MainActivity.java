package com.oneul;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.extra.DateTime;
import com.oneul.fragment.DialogFragment;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.StatFragment;
import com.oneul.service.RealService;

public class MainActivity extends AppCompatActivity {
    //    ㄴㄴ 데이터
    public static String showDay = DateTime.today();
    public static boolean useEditMemo = false;
    boolean doubleBackToExitPressedOnce = false;

    //    ㄴㄴ 프래그먼트
    FragmentManager manager = getSupportFragmentManager();
    Fragment f_home, f_stat, f_setting;

    public static void focusClear(Activity activity, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = activity.getCurrentFocus();

            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    if (v.hasFocus()) {
                        v.getRootView().requestFocus();
                    }

                    ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }

    @SuppressLint({"NonConstantResourceId", "BatteryLife"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ㄴㄴ 서비스
//        전원 설정
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());

        if (!isWhiteListing) {
            Intent intent = new Intent()
                    .setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }
//        서비스가 없으면
        SharedPreferences preferences = getSharedPreferences("sFile", MODE_PRIVATE);
        RealService.fixNoti = preferences.getBoolean("fixNoti", true);

        if (RealService.fixNoti) {
            RealService.startForeground(this);
        }

//        하단메뉴 클릭 시
        ((BottomNavigationView) findViewById(R.id.bot_menu)).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                메모 작성중일 시
                if (useEditMemo) {
                    DialogFragment.checkMemoDialog(MainActivity.this, item.getItemId());

                    return false;
                } else {
//                선택한 화면으로 전환
                    switch (item.getItemId()) {
                        case R.id.bot_menu_home:
//                            프래그먼트 없으면 생성, 있으면 보이기
                            if (f_home == null) {
                                f_home = HomeFragment.newInstance();
                                manager.beginTransaction().add(R.id.container, f_home).commit();
                            }

                            if (f_home.isVisible() && !TextUtils.equals(showDay, DateTime.today())) {
                                showDay = DateTime.today();
                                manager.beginTransaction().detach(f_home).attach(f_home).commit();

                                return true;
                            } else {
                                manager.beginTransaction().show(f_home).commit();
                            }

                            if (f_stat != null) manager.beginTransaction().hide(f_stat).commit();
                            if (f_setting != null)
                                manager.beginTransaction().hide(f_setting).commit();

                            return true;

                        case R.id.bot_menu_stat:
//                            프래그먼트 없으면 생성, 있으면 보이기
                            if (f_stat == null) {
                                f_stat = StatFragment.newInstance();
                                manager.beginTransaction().add(R.id.container, f_stat).commit();
                            }

                            manager.beginTransaction().show(f_stat).commit();
                            if (f_home != null) manager.beginTransaction().hide(f_home).commit();
                            if (f_setting != null)
                                manager.beginTransaction().hide(f_setting).commit();

                            return true;

                        case R.id.bot_menu_setting:
//                            프래그먼트 없으면 생성, 있으면 보이기
                            if (f_setting == null) {
                                f_setting = SettingFragment.newInstance();
                                manager.beginTransaction().add(R.id.container, f_setting).commit();
                            }

                            manager.beginTransaction().show(f_setting).commit();
                            if (f_home != null) manager.beginTransaction().hide(f_home).commit();
                            if (f_stat != null) manager.beginTransaction().hide(f_stat).commit();

                            return true;

                        default:
                            return false;
                    }
                }
            }
        });

//        시작 시 홈화면 불러오기
        f_home = HomeFragment.newInstance();
        manager.beginTransaction().replace(R.id.container, f_home).commit();
    }

    //    포커스 초기화
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        focusClear(this, ev);

        return super.dispatchTouchEvent(ev);
    }

    //    뒤로가기 종료
    @Override
    public void onBackPressed() {
//        메모 작성중일 시
        if (useEditMemo) {
            DialogFragment.checkMemoDialog(MainActivity.this, 0);
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
}

//todo 다음날에 완료할 때 로직 및 화면, 슬라드 날짜 변경