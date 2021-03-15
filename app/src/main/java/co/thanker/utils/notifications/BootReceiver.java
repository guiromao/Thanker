package co.thanker.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {

        // Your code to execute when Boot Completd

        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);

        //Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show();

    }

}