package com.google.developer.colorvalue;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.developer.colorvalue.service.NotificationJobService;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int JOB_ID = 88;

    private static final int INTERVAL_IN_HOURS = 15;
    private static final int INTERVAL_FLEX_TIME_IN_MINUTES=25;
    private static final int BACK_OFF_TIMING_IN_SECONDS=30;

    private static final long BACK_OFF_TIMING_MILLIS = TimeUnit.SECONDS.toMillis(BACK_OFF_TIMING_IN_SECONDS);
    private static final long SYNC_INTERVAL_MILLIS =  TimeUnit.MINUTES.toMillis(INTERVAL_IN_HOURS);
    private static final long SYNC_INTERVAL_FLEX_TIME_MILLIS = TimeUnit.MINUTES.toMillis(INTERVAL_FLEX_TIME_IN_MINUTES);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String notifyKey = getString(R.string.pref_key_notification);

        if (key.equals(notifyKey)) {

            boolean on = sharedPreferences.getBoolean(notifyKey, false);

            /*  Get the reference to the job scheduler  */
            JobScheduler jobScheduler =
                    (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if(on) {
                /*  If turned on, set the flag in shared preferences   */
//                NotificationUtils.setIsFirstNotification(this,true);

                /*  Define the job using the JobInfo.Builder    */
                JobInfo.Builder jobInfoBuilder =
                        new JobInfo.Builder(JOB_ID, new ComponentName(this, NotificationJobService.class))
                                .setPersisted(true)
                                .setBackoffCriteria(BACK_OFF_TIMING_MILLIS,JobInfo.BACKOFF_POLICY_EXPONENTIAL);

                /*  Use periodic job with flex time if greater than API 24  */
                if(Build.VERSION.SDK_INT >= 24){
                    jobInfoBuilder.setPeriodic(SYNC_INTERVAL_MILLIS,SYNC_INTERVAL_FLEX_TIME_MILLIS);
                }else {
                    jobInfoBuilder.setPeriodic(SYNC_INTERVAL_MILLIS);
                }

                /*  Build the job   */
                JobInfo jobInfo = jobInfoBuilder.build();

                Log.d(TAG,"Set time "+SYNC_INTERVAL_MILLIS);

                /*  Schedule the job    */
                if (jobScheduler != null) {
                    int result = jobScheduler.schedule(jobInfo);

                    if(result == JobScheduler.RESULT_SUCCESS){
                        Log.d(TAG,"Scheduled job");
                    }
                }

            }
            else {

                /*  Notifications turned off, cancel the job    */
                jobScheduler.cancel(JOB_ID);
            }

            //  COMPLETED :implement JobScheduler for notification {@link ScheduledJobService}
        }
    }

    /**
     * loading setting resource
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
        }

    }
}