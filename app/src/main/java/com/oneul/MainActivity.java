package com.oneul;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.extra.DateTime;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.StatFragment;

public class MainActivity extends AppCompatActivity {
    //    데이터 저장
    public static String inputText;
    public static String showDay;

    //    하단 메뉴
    public static BottomNavigationView bot_menu;

    //    뒤로가기 종료
    boolean doubleBackToExitPressedOnce = false;

    //    에딧 텍스트 클리어 포커스, 키보드 내리기
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

//        하단 메뉴
        bot_menu = findViewById(R.id.bot_menu);

//        시작 시 홈화면 불러오기
        showDay = DateTime.today();
        openFragment(HomeFragment.newInstance());

//        하단 메뉴 클릭 시
        bot_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                입력하던 투데이 박스 값 저장
                inputText = HomeFragment.et_todayBox.getText().toString();

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

                return false;
            }
        });
    }

    //    뒤로가기 종료
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "종료하려면 한 번 더 누르세요.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //    화면 전환
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}