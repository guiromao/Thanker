package co.thanker.utils.notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtils.setupNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    /*private void startAlarm(Context context/*boolean isNotification, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        //THIS IS WHERE YOU SET NOTIFICATION TIME FOR CASES WHEN THE NOTIFICATION NEEDS TO BE RESCHEDULED
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE,45);

        myIntent = new Intent(this, ThankReminderPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    } */
}