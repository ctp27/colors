package com.google.developer.colorvalue.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.developer.colorvalue.CardActivity;
import com.google.developer.colorvalue.R;
import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.data.CardProvider;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class CardAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        CardAppWidgetService.updateCardWidgets(context);
    }

    public static void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int widgetId : appWidgetIds) {
            updateWidget(context, widgetId);
        }
    }

    private static void updateWidget(Context context, int widgetId) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(CardProvider.Contract.CONTENT_URI,
                null, null, null, null);

        int cardCount;
        if (cursor != null) {
            cardCount = cursor.getCount();
        } else {
            Log.w(TAG, "Unable to read card database");
            return;
        }

        Random random = new Random();
        int randomNumber = random.nextInt(cardCount);

        cursor.moveToPosition(randomNumber);

        Card card = new Card(cursor);
        cursor.close();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.card_widget);

        views.setTextViewText(R.id.widget_text, card.getHex());
        views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor(card.getHex()));

        Intent intent = new Intent(context, CardActivity.class);
        intent.putExtra(CardActivity.INTENT_PARCEL_EXTRA,card);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent openCardIntent = taskStackBuilder
                .getPendingIntent(randomNumber, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_background,openCardIntent);

        // Instruct the widget manager to update the widget
        widgetManager.updateAppWidget(widgetId, views);
    }
}

