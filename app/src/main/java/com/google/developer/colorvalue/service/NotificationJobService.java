package com.google.developer.colorvalue.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.google.developer.colorvalue.utils.NotificationUtils;

public class NotificationJobService extends JobService {

    private AsyncTask<Void,Void,Void> mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters params) {
        //  notification

        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                 NotificationUtils.displayStudyReminderNotification(NotificationJobService.this);
                 return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(params,false);
            }
        };

        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        if(mAsyncTask!=null){
            mAsyncTask.cancel(true);
        }
        return true;
    }

}