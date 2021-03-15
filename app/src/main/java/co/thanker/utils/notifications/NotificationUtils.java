package co.thanker.utils.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;
import java.util.Date;

import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.utils.notifications.ThankReminderFirebaseJobService;
import co.thanker.utils.notifications.ThankReminderPublisher;

public class NotificationUtils {

    private static final String THANKING_NOTIFICATION_CHANNEL_ID = "thanking-notification-channel";
    private static final String BIRTHDAY_NOTIFICATION_CHANNEL_ID = "birthday-notification-channel";
    private static final String REMINDER_JOB_THANKING_TAG = "reminder-job-thanking-tag";

    private static final int THANKING_PENDING_INTENT_ID = 22;
    private static final int BIRTHDAY_PENDING_INTENT_ID = 33;
    private static final int PERIODICITY = 2 * 24 * 60 * 60;
    private static final int TOLERANCE = 60 * 60;
    private static final int REMINDER_INTERVAL_IN_SECONDS = 2 * 24 * 60 * 60;

    private static boolean sInitialized;

    public static PendingIntent thankingIntent(Context context){
        Intent thankActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context, THANKING_PENDING_INTENT_ID, thankActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static Bitmap getLargeIcon(Context context){
        Resources res = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.logot);

        return largeIcon;
    }


    public static void sendThankReminder(Context context/*, long delay*/){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nChannel = new NotificationChannel(THANKING_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel_thanking),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(nChannel);

        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, THANKING_NOTIFICATION_CHANNEL_ID)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.logot)
                .setLargeIcon(getLargeIcon(context))
                //.setContentTitle(context.getString(R.string.notification_title))
                //.setContentText(context.getString(R.string.notification_text))
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(thankingIntent(context))
                .addAction(new NotificationCompat.Action(R.drawable.logot, context.getString(R.string.open_thanker), thankingIntent(context)))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        /*Notification notification = notificationBuilder.build();

        Intent notificationIntent = new Intent(context, ThankReminderPublisher.class);
        //notificationIntent.putExtra(ThankReminderPublisher.NOTIFICATION_ID, THANKING_PENDING_INTENT_ID);
        notificationIntent.putExtra(REMINDER_JOB_THANKING_TAG, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, THANKING_PENDING_INTENT_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);*/

        notificationManager.notify(THANKING_PENDING_INTENT_ID, notificationBuilder.build());
    }

    synchronized public static void scheduleThankReminder(@NonNull final Context context){
        if(!sInitialized){
            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job constraintReminderJob = dispatcher.newJobBuilder()
                    .setService(ThankReminderFirebaseJobService.class)
                    .setTag(REMINDER_JOB_THANKING_TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(PERIODICITY, (PERIODICITY + TOLERANCE)))
                    .setReplaceCurrent(true)
                    .build();

            dispatcher.schedule(constraintReminderJob);
            sInitialized = true;
        }
    }

    public static void setupNotification(Context context/*boolean isNotification, boolean isRepeat*/) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        // NOTIFICATION WILL BE SENT FOR THE DAY AFTER, AT 19H00: THANKING TIME
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 00);

        myIntent = new Intent(context, ThankReminderPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(context,THANKING_PENDING_INTENT_ID, myIntent,0);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
