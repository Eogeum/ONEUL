package com.oneul.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.RemoteInput;

import com.oneul.MainActivity;
import com.oneul.extra.DateTime;
import com.oneul.fragment.HomeFragment;
import com.oneul.oneul.Oneul;

public class OneulReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle;

        Log.d("TAG", String.valueOf(intent.getExtras().getInt("requestCode")));

        switch (intent.getExtras().getInt("requestCode")) {
            case 0:
                bundle = RemoteInput.getResultsFromIntent(intent);

                if (bundle != null) {
                    HomeFragment.dbHelper.addOneul(DateTime.today(), DateTime.nowTime(), null,
                            bundle.getCharSequence("KEY_OTITLE").toString(), null, 0);
                }
                break;

            case 1:
                bundle = RemoteInput.getResultsFromIntent(intent);

                if (bundle != null) {
                    Oneul oneul = HomeFragment.dbHelper.getStartOneul();
                    int oNo = oneul.getoNo();
                    String oMemo;

                    if (oneul.getoMemo() != null) {
                        oMemo = oneul.getoMemo() + "\n" + DateTime.nowTime() + " " + bundle.getCharSequence("KEY_OMEMO");
                    } else {
                        oMemo = DateTime.nowTime() + " " + bundle.getCharSequence("KEY_OMEMO");
                    }

                    HomeFragment.dbHelper.editMemo(oNo, oMemo);
                    Toast.makeText(context, "메모를 추가했습니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case 2:
                Oneul oneul = HomeFragment.dbHelper.getStartOneul();
                int oNo = oneul.getoNo();

                HomeFragment.dbHelper.endOneul(oNo, DateTime.nowTime());
                Toast.makeText(context, MainActivity.showDay + "\n일과를 저장했습니다.",
                        Toast.LENGTH_LONG).show();
        }

//        서비스 재시작
        context.stopService(RealService.serviceIntent);
        context.startService(new Intent(context, RealService.class));
    }
}