package edu.rosehulman.scottae.justcheckingin.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public NotificationBroadcastReceiver() {
        //empty constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(Constants.KEY_NOTIFICATION);
        int id = intent.getIntExtra(Constants.KEY_SOON_NOTIFICATION_ID, 0);
        assert manager != null;
        manager.notify(id, notification);
    }
}
