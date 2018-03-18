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
import com.google.developer.colorvalue.utils.NotificationUtils;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int JOB_ID = 88;

    private static final int INTERVAL_IN_HOURS = 22;
    private static final int INTERVAL__IN_MINUTES = 15;
    private static final long BACK_OFF_TIMING = TimeUnit.MINUTES.toMillis(2);
    private static final long SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toMillis(INTERVAL_IN_HOURS);
    private static final long SYNC_INTERVAL_SECONDS_TEST = TimeUnit.MINUTES.toMillis(INTERVAL__IN_MINUTES);
    private static final long SYNC_INTERVAL_FLEX_TIME = TimeUnit.MINUTES.toMillis(20);

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

            JobScheduler jobScheduler =
                    (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(on) {
                NotificationUtils.setIsFirstNotification(this,true);
                JobInfo.Builder jobInfoBuilder =
                        new JobInfo.Builder(JOB_ID, new ComponentName(this, NotificationJobService.class))
                                .setPeriodic(SYNC_INTERVAL_SECONDS_TEST)
                                .setPersisted(true)
                                .setBackoffCriteria(BACK_OFF_TIMING,JobInfo.BACKOFF_POLICY_EXPONENTIAL);

                if(Build.VERSION.SDK_INT >= 24){
                    jobInfoBuilder.setPeriodic(SYNC_INTERVAL_SECONDS_TEST,SYNC_INTERVAL_FLEX_TIME);
                }

                JobInfo jobInfo = jobInfoBuilder.build();

                Log.d(TAG,"Set time "+SYNC_INTERVAL_SECONDS_TEST);

                if (jobScheduler != null) {
                    jobScheduler.schedule(jobInfo);
                    Log.d(TAG,"Scheduled job");
                }

            }
            else {
                jobScheduler.cancel(JOB_ID);
            }
            //  implement JobScheduler for notification {@link ScheduledJobService}
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