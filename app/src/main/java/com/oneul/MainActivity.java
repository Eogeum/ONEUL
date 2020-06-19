package com.oneul;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
    Integer selectedItem = R.id.bot_menu_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        하단 메뉴
        bot_menu = findViewById(R.id.bot_menu);
        bot_menu.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


//        데이터 불러오기
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt("selectedItem");

            switch (selectedItem) {
                case R.id.bot_menu_write:
                    openFragment(WriteFragment.newInstance("", ""));
                    break;
                case R.id.bot_menu_setting:
                    openFragment(SettingFragment.newInstance("", ""));
                    break;
            }
        } else {
            openFragment(HomeFragment.newInstance("", ""));
        }


    }

//    뒤로가기 종료 메서드
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
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
    }

//    todo : 나중에 봐야할 코드
//    화면전환
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedItem = item.getItemId();

                    switch (item.getItemId()) {
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
            };

//    하단 전환
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}