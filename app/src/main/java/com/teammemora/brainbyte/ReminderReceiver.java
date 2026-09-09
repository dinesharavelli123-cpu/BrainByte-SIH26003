package com.teammemora.brainbyte;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL = "brainbyte_reminders";
    @Override public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String id = intent.getStringExtra("id");
        if (title == null || title.isEmpty()) title = "BrainByte reminder";
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "BrainByte Reminders", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Daily BrainByte reminders");
            nm.createNotificationChannel(ch);
        }
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("BrainByte")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);
        int nid;
        try { nid = (int)(Long.parseLong(id == null ? "1" : id) & 0x7fffffff); } catch(Exception e) { nid = Math.abs((id == null ? title : id).hashCode()); }
        nm.notify(nid, b.build());
    }
}
