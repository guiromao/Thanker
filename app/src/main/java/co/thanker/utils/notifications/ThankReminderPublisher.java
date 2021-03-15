package co.thanker.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import co.thanker.MainActivity;
import co.thanker.R;
import co.thanker.utils.TextUtils;

public class ThankReminderPublisher extends BroadcastReceiver {

    private static final String TAG = ThankReminderPublisher.class.getSimpleName();
    private static final String THANKING_NOTIFICATION_CHANNEL_ID = "thanking-notification-channel";
    private static final String REMINDER_JOB_THANKING_TAG = "reminder-job-thanking-tag";

    private static final int THANKING_PENDING_INTENT_ID = 22;
    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(final Context context, Intent intent) {

        /*NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(REMINDER_JOB_THANKING_TAG);
        //int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(THANKING_PENDING_INTENT_ID, notification); */

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nChannel = new NotificationChannel(THANKING_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel_thanking),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(nChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, THANKING_NOTIFICATION_CHANNEL_ID);

        Intent myIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                THANKING_PENDING_INTENT_ID,
                myIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String title = TextUtils.getStringFromArray(context.getResources().getStringArray(R.array.notification_titles));
        String text = TextUtils.getStringFromArray(context.getResources().getStringArray(R.array.notification_bodies));

        Log.v(TAG, "Notifications Utils. Title: " + title);
        Log.v(TAG, "Notifications Utils. Body: " + text);

        builder.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.thankyou3)
                .setLargeIcon(NotificationUtils.getLargeIcon(context))
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .addAction(new NotificationCompat.Action(R.drawable.logot, context.getString(R.string.open_thanker),
                        NotificationUtils.thankingIntent(context)));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(THANKING_PENDING_INTENT_ID, builder.build());
    }
}
