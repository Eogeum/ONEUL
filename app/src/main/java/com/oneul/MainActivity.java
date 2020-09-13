package com.oneul;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oneul.extra.BitmapChanger;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.fragment.DialogFragment;
import com.oneul.fragment.HomeFragment;
import com.oneul.fragment.SettingFragment;
import com.oneul.fragment.StatFragment;
import com.oneul.service.RealService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //    ㄴㄴ 데이터
    public static String showDay = DateTime.today();
    public static boolean useEditMemo = false;
    boolean doubleBackToExitPressedOnce = false;

    //    ㄴㄴ 뷰
    BottomNavigationView bot_menu;

    //    ㄴㄴ 디비
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    ㄴㄴ 디비
        dbHelper = DBHelper.getDB(this);

//        ㄴㄴ 서비스
//        전원 설정
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());

        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }
//        서비스가 없으면
        if (RealService.serviceIntent == null) {
            RealService.serviceIntent = new Intent(this, RealService.class);
            startService(RealService.serviceIntent);
        }

//        하단메뉴 클릭 시
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

//        시작 시 홈화면 불러오기
        openFragment(HomeFragment.newInstance());
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
                    if (v.hasFocus()) {
                        v.getRootView().requestFocus();
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //    뒤로가기 종료
    @Override
    public void onBackPressed() {
//        메모 작성중일 시
        if (useEditMemo) {
            DialogFragment.editMemoDialog(MainActivity.this, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            switch (requestCode) {
                case DialogFragment.CAMERA_REQUEST_CODE:
//                    미디어 스캔
                    MediaScannerConnection.scanFile(this, new String[]{DialogFragment.currentPhotoPath},
                            null, null);

                    bitmap = BitmapFactory.decodeFile(DialogFragment.currentPhotoPath);
                    try {
                        ExifInterface ei = new ExifInterface(DialogFragment.currentPhotoPath);

                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch(orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bitmap = rotateImage(bitmap, 270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case DialogFragment.GALLERY_REQUEST_CODE:
                    bitmap = BitmapChanger.getBitmap(data.getData(), this);
                    break;
            }

            bitmap = BitmapChanger.checkAndResize(bitmap);
            dbHelper.addPhoto(dbHelper.getStartOneul().getoNo(), BitmapChanger.bitmapToBytes(bitmap));
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        todo 효율적인 퍼미션 체크로 변경
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
            DialogFragment.selectorDialog(this);
        }
    }
}

//todo 슬라드 날짜 변경
//todo : 1일날 시작한 일과를 2일에 완료하면 화면에 일과 시간을 어떻게 표시할 지