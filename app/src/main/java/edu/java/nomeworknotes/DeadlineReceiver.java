package edu.java.nomeworknotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeadlineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String noteTitle = intent.getStringExtra("note_title");
        if (noteTitle != null) {
            NotificationHelper.showDeadlineNotification(context, noteTitle);
        }
    }
}