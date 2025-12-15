package edu.java.nomeworknotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;
import java.util.List;

public class DeadlineNotificationService {
    
    public static void scheduleDeadlineNotifications(Context context, List<Note> notes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (Note note : notes) {
            String deadline = note.getDeadline();
            if (deadline != null && !deadline.equals(context.getString(R.string.deadline_not_set))) {
                Calendar deadlineTime = parseDeadline(deadline);
                if (deadlineTime != null && deadlineTime.getTimeInMillis() > System.currentTimeMillis()) {
                    Intent intent = new Intent(context, DeadlineReceiver.class);
                    intent.putExtra("note_title", note.getTitle());
                    
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, 
                        (int) note.getId(),
                        intent, 
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                            deadlineTime.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                            deadlineTime.getTimeInMillis(), pendingIntent);
                    }
                }
            }
        }
    }
    
    public static Calendar parseDeadline(String deadline) {
        try {
            if (deadline.contains(" ")) {
                String[] dateTimeParts = deadline.split(" ");
                if (dateTimeParts.length == 2) {
                    String datePart = dateTimeParts[0];
                    String timePart = dateTimeParts[1];

                    String[] dateParts = datePart.split("\\.");
                    if (dateParts.length == 3) {
                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-based
                        int year = Integer.parseInt(dateParts[2]);

                        String[] timeParts = timePart.split(":");
                        if (timeParts.length == 2) {
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);
                            
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day, hour, minute, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            
                            return calendar;
                        }
                    }
                }
            } else {
                String[] parts = deadline.split("\\.");
                if (parts.length == 3) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int year = Integer.parseInt(parts[2]);
                    
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 23, 59, 59);
                    calendar.set(Calendar.MILLISECOND, 0);
                    
                    return calendar;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
