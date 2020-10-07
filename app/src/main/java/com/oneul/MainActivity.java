package com.oneul;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.extra.DateTime;
import com.oneul.fragment.DialogFragment;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.StatFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //    ㄴㄴ 리퀘스트 코드
    final int CAMERA_REQUEST_CODE = 101;
    final int GALLERY_REQUEST_CODE = 202;

    //    ㄴㄴ 데이터 저장
    public static String inputText;
    public static String showDay = DateTime.today();
    public static boolean useEditMemo = false;

    //    ㄴㄴ 하단 메뉴
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

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Fragment fragmentId = getSupportFragmentManager().findFragmentById(R.id.container);
//        같은 탭을 누를 시 쇼데이 초기화
        if (fragmentId != null) {
            if (fragment.getClass() == fragmentId.getClass()) {
                showDay = DateTime.today();
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:

//                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    String string = DialogFragment.currentPhotoPath;
//                    File f = new File(string);
//                    Uri uri = Uri.fromFile(f);
//                    mediaScanIntent.setData(uri);
//                    sendBroadcast(mediaScanIntent);
                    imageView.setImageURI(Uri.parse(DialogFragment.currentPhotoPath));

                    break;

                case GALLERY_REQUEST_CODE:


            }
        }

    }
}

//todo 슬라드 날짜 변경
//todo 사진 저장 취소 아이콘
// fixme : 화면에 날짜를 표시할 방법
// fixme : 1일날 시작한 일과를 2일에 완료하면 화면에 일과 시간을 어떻게 표시할 지