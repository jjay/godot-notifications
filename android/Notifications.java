package org.godotengine.godot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.content.Intent;
import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.content.pm.ApplicationInfo;
import com.godot.game.R;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.lang.System;
import android.os.Build;


public class Notifications extends Godot.SingletonBase {

    Application app;
    Bitmap large_icon;

    public float getUptime(){
        return 0.001f*SystemClock.elapsedRealtime();
    }

    public String getAndroidVersion(){
        return Build.VERSION.RELEASE;
    }

    public void notify(int id, String title, String text, int delay) {
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(app)
            .setSmallIcon(R.drawable.icon)
            //.setLargeIcon(large_icon)
            .setContentTitle(title)
            .setWhen(delay*1000 + System.currentTimeMillis())
            .setShowWhen(true)
            .setContentText(text)
            

            //.setVisibility(1)
            .setAutoCancel(true);
        final Intent notificationIntent = new Intent(app, Godot.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi = PendingIntent.getActivity(app, 0, notificationIntent, 
                                   PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);

        Notification notification = mBuilder.build();
        Intent delayIntent = new Intent(app, NotificationPublisher.class);
        delayIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        delayIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(app, id, delayIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + 1000*(long)delay;
        AlarmManager alarmManager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void cancel(int id){
        Intent delayIntent = new Intent(app, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(app, id, delayIntent, 0);
        AlarmManager alarmManager = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return new Notifications(p_activity);
    }

    public Notifications(Activity p_activity) {
        registerClass("Notifications", new String[]{"notify", "cancel", "getUptime", "getAndroidVersion"});

        app = p_activity.getApplication();
        large_icon = BitmapFactory.decodeResource(app.getResources(), R.drawable.icon);
    }
}
