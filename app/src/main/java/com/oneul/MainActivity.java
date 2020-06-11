package com.oneul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.WriteFragment;

public class MainActivity extends AppCompatActivity {
    //      변수 선언
    BottomNavigationView bot_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        하단 메뉴 연결 및 선언
        bot_menu = findViewById(R.id.bot_menu);
        bot_menu.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));
    }

//        화면 전환 메서드
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
}