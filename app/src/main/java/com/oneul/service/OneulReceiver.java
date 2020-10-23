package com.oneul.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.RemoteInput;

import com.oneul.MainActivity;
import com.oneul.extra.DBHelper;
import com.oneul.extra.DateTime;
import com.oneul.oneul.Oneul;

import java.util.Objects;

public class OneulReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = DBHelper.getDB(context);
        Bundle bundle;

        switch (Objects.requireNonNull(intent.getExtras()).getInt("requestCode")) {
            case 0:
                bundle = RemoteInput.getResultsFromIntent(intent);

                if (bundle != null) {
                    dbHelper.startOneul(context, DateTime.today(), DateTime.nowTime(), null,
                            Objects.requireNonNull(bundle.getCharSequence("KEY_OTITLE")).toString(),
                            null, null, 0);
                }
                break;

            case 1:
                bundle = RemoteInput.getResultsFromIntent(intent);

                if (bundle != null) {
                    Oneul oneul = dbHelper.getStartOneul();
                    int oNo = oneul.getoNo();
                    String oMemo;

                    if (oneul.getoMemo() != null) {
                        oMemo = oneul.getoMemo() + "\n" + DateTime.nowTime() + " " +
                                bundle.getCharSequence("KEY_OMEMO");
                    } else {
                        oMemo = DateTime.nowTime() + " " + bundle.getCharSequence("KEY_OMEMO");
                    }

                    dbHelper.editMemo(oNo, oMemo);
                    Toast.makeText(context, "메모를 추가했습니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case 2:
                Oneul oneul = dbHelper.getStartOneul();
                int oNo = oneul.getoNo();

                dbHelper.endOneul(oNo, DateTime.nowTime());
                Toast.makeText(context, MainActivity.showDay + "\n일과를 저장했습니다.", Toast.LENGTH_LONG).show();
                break;
        }

//        알림 새로고침
        context.getSystemService(NotificationManager.class).notify(101, RealService.createNotification(context));
    }
}