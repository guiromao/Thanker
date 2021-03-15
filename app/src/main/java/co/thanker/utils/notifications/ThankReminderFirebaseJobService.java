package co.thanker.utils.notifications;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ThankReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(@NonNull com.firebase.jobdispatcher.JobParameters job) {
        final JobParameters newParams = job;

        mBackgroundTask = new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = ThankReminderFirebaseJobService.this;
                NotificationUtils.sendThankReminder(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o){
                jobFinished(newParams, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(@NonNull com.firebase.jobdispatcher.JobParameters job) {
        if(mBackgroundTask != null){
            mBackgroundTask.cancel(true);
        }
        return true;
    }


}
