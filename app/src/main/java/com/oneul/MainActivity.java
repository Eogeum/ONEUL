package com.oneul;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.WriteFragment;

public class MainActivity extends AppCompatActivity {

    //    뒤로가기 종료
    boolean doubleBackToExitPressedOnce = false;

//    하단 메뉴
    BottomNavigationView bot_menu;

//    데이터 저장
    int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        하단 메뉴
        bot_menu = (BottomNavigationView) findViewById(R.id.bot_menu);
        bot_menu.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

//        데이터 불러오기
        selectedItem = R.id.bot_menu_home;

        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt("selectedItem");
        }

        changeFrag(selectedItem);


    }

//    뒤로가기 종료 메서드
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "종료하려면 한 번 더 누르세요.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

//    데이터 저장
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem", selectedItem);

//        todo : 투데이박스 스타트박스 입력값, 메모박스 가시성, 키보드 상태 추가
    }

//    바텀 메뉴 클릭 시
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedItem = item.getItemId();

                    return changeFrag(item.getItemId());
                }
            };

//    화면 전환 메서드
    private boolean changeFrag(int selectedItem) {
        switch (selectedItem) {
            case R.id.bot_menu_home:
                openFragment(HomeFragment.newInstance("", ""));
                return true;

            case R.id.bot_menu_write:
                openFragment(WriteFragment.newInstance("", ""));
                return true;

            case R.id.bot_menu_setting:
                openFragment(SettingFragment.newInstance("", ""));
                return true;
        }

        return false;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}