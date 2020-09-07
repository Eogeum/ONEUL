package com.oneul.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.oneul.MainActivity;
import com.oneul.R;
import com.oneul.fragment.HomeFragment;

import java.util.Calendar;

public class RealService extends Service {
    public static Intent serviceIntent = null;

    public RealService() {
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "101")
                .setSmallIcon(R.drawable.ic_home1)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(false)
                .setOngoing(true)
                .setColor(Color.parseColor("#E88346"))
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        Intent mintent = new Intent(this, OneulReceiver.class);

        //            기록중인 일과 있으면
        if (HomeFragment.dbHelper.getStartOneul() != null) {
            RemoteInput.Builder remoteInput = new RemoteInput.Builder("KEY_OMEMO")
                    .setLabel("추가할 메모를 입력하세요.");

            mintent.putExtra("requestCode", 1);
            PendingIntent editMemoIntent = PendingIntent.getBroadcast(this, 1, mintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action editMemoAction = new NotificationCompat.Action.Builder(null, "메모", editMemoIntent)
                    .addRemoteInput(remoteInput.build())
                    .setAllowGeneratedReplies(true)
                    .build();

            mintent.putExtra("requestCode", 2);
            PendingIntent stopOneulIntent = PendingIntent.getBroadcast(this, 2, mintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(HomeFragment.dbHelper.getStartOneul().getoTitle())
                    .setSubText("진행 중")
                    .setContentText(HomeFragment.dbHelper.getStartOneul().getoStart())
                    .addAction(editMemoAction)
                    .addAction(0, "STOP", stopOneulIntent);
        } else {
            RemoteInput.Builder remoteInput = new RemoteInput.Builder("KEY_OTITLE")
                    .setLabel("일과 제목을 입력하세요.");

            mintent.putExtra("requestCode", 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action startOneulAction = new NotificationCompat.Action.Builder(null, "START", pendingIntent)
                    .addRemoteInput(remoteInput.build())
                    .setAllowGeneratedReplies(true)
                    .build();

            builder.setContentTitle("새로운 일과를 시작해보세요.")
                    .addAction(startOneulAction);
        }

        startForeground(101, builder.build());

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = null;
        setAlarmTimer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,
                new Intent(this, AlarmReceiver.class), 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }
}