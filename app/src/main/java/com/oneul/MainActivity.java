package com.oneul;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.WriteFragment;

import static com.oneul.fragment.HomeFragment.keyboardShow;

public class MainActivity extends AppCompatActivity {
//    뷰
    View root;

//    하단 메뉴
    BottomNavigationView bot_menu;

//    뒤로가기 종료
    boolean doubleBackToExitPressedOnce = false;

//    데이터 저장
    int selectedItem = R.id.bot_menu_home;;
    String inputText;
    boolean keyboardShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        뷰
        root = this.getWindow().getDecorView().getRootView();

//        하단 메뉴
        bot_menu = (BottomNavigationView) findViewById(R.id.bot_menu);
        bot_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedItem = item.getItemId();
                inputText = HomeFragment.et_todayBox.getText().toString();

                return changeFrag(item.getItemId(), inputText, keyboardShow);
            }
        });

//        데이터 불러오기
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt("selectedItem");
            inputText = savedInstanceState.getString("inputText");
            keyboardShow = savedInstanceState.getBoolean("keyboardShow");
        }

        changeFrag(selectedItem, inputText, keyboardShow);

//        키보드 리스너
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        root.getWindowVisibleDisplayFrame(r);
                        int screenHeight = root.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) {
                            keyboardShow = true;
                        } else {
                            keyboardShow = false;
                        }
                    }
                });


        keyboardShow(this, HomeFragment.et_todayBox);
    }

//    뒤로가기 종료
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
        outState.putString("inputText", HomeFragment.et_todayBox.getText().toString());
        outState.putBoolean("keyBoardShow", keyboardShow);

//        todo : 스타트박스 입력값, 메모박스 가시성, 키보드 상태 추가
    }

//    화면 전환
    private boolean changeFrag(int selectedItem, String inputText, boolean keyboardShow) {
        switch (selectedItem) {
            case R.id.bot_menu_home:
                openFragment(HomeFragment.newInstance(inputText, keyboardShow));
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

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}