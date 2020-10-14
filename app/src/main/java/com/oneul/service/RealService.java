package com.oneul.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.oneul.CameraActivity;
import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.extra.DBHelper;

public class RealService extends Service {
    public static Intent serviceIntent = null;

    public RealService() {
    }

    public static Notification createNotification(Context context) {
        DBHelper dbHelper = DBHelper.getDB(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "101")
                .setSmallIcon(R.drawable.ic_main)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .setColor(Color.parseColor("#E88346"))
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        Intent mintent = new Intent(context, OneulReceiver.class);

        //            기록중인 일과 있으면
        if (dbHelper.getStartOneul() != null) {
            RemoteInput.Builder remoteInput = new RemoteInput
                    .Builder("KEY_OMEMO")
                    .setLabel("추가할 메모를 입력하세요.");

            mintent.putExtra("requestCode", 1);
            PendingIntent editMemoIntent = PendingIntent
                    .getBroadcast(context, 1, mintent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action editMemoAction = new NotificationCompat.Action
                    .Builder(null, "메모", editMemoIntent)
                    .addRemoteInput(remoteInput.build())
                    .setAllowGeneratedReplies(true)
                    .build();


            mintent.putExtra("requestCode", 2);
            PendingIntent stopOneulIntent = PendingIntent
                    .getBroadcast(context, 2, mintent, PendingIntent.FLAG_UPDATE_CURRENT);

            mintent = new Intent(context, CameraActivity.class);
            PendingIntent addPhotoIntent = PendingIntent
                    .getActivity(context, 3, mintent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(dbHelper.getStartOneul().getoTitle())
                    .setSubText("진행 중")
                    .setContentText(dbHelper.getStartOneul().getoStart())
                    .addAction(editMemoAction)
                    .addAction(0, "사진", addPhotoIntent)
                    .addAction(0, "STOP", stopOneulIntent);
        } else {
            RemoteInput.Builder remoteInput = new RemoteInput.Builder("KEY_OTITLE")
                    .setLabel("일과 제목을 입력하세요.");

            mintent.putExtra("requestCode", 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action startOneulAction = new NotificationCompat.Action
                    .Builder(null, "START", pendingIntent)
                    .addRemoteInput(remoteInput.build())
                    .setAllowGeneratedReplies(true)
                    .build();

            builder.setContentTitle("새로운 일과를 시작해보세요.")
                    .addAction(startOneulAction);
        }

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        //        오레오 이상이면 채널 만들기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("101", "고정",
                    NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(101, createNotification(this));

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = new Intent(this, RealService.class);
        startService(serviceIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}