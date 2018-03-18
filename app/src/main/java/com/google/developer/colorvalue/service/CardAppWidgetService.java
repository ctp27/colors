package com.google.developer.colorvalue.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

/**
 * Created by clinton on 3/17/18.
 */

public class CardAppWidgetService extends JobIntentService {

    private static final String ACTION_HANDLE_UPDATE_WIDGET= "update-widgets";


    public static void updateCardWidgets(Context context){
        Intent intent = new Intent(context,CardAppWidgetService.class);
        intent.setAction(ACTION_HANDLE_UPDATE_WIDGET);
        enqueueWork(context,CardAppWidgetService.class,0,intent);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        switch (intent.getAction()){
            case ACTION_HANDLE_UPDATE_WIDGET:
                handleActionUpdateWidgets();
        }
    }

    private void handleActionUpdateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CardAppWidget.class));
        CardAppWidget.updateAllWidgets(this,appWidgetManager,appWidgetIds);
    }
}
