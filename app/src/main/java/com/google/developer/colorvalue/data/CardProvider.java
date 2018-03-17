package com.google.developer.colorvalue.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;
import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

public class CardProvider extends ContentProvider {

    /** Matcher identifier for all cards */
    private static final int CARD = 100;
    /** Matcher identifier for one card */
    private static final int CARD_WITH_ID = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.google.developer.colorvalue/cards
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME, CARD);
        // content://com.google.developer.colorvalue/cards/#
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME + "/#", CARD_WITH_ID);
    }

    private CardSQLite mCardSQLite;

    @Override
    public boolean onCreate() {
        mCardSQLite = new CardSQLite(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
            @Nullable String selection, @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        //  Implement query function by Uri all cards or single card by id

        Cursor returnCursor=null;

        switch (sUriMatcher.match(uri)){
            case CARD:
                returnCursor= mCardSQLite.getReadableDatabase().query(TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CARD_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArgumentsArray = new String[]{id};
                String selectionString = Contract.Columns._ID + " = ? ";
                returnCursor = mCardSQLite.getReadableDatabase().query(TABLE_NAME,
                        projection,selectionString,selectionArgumentsArray,null,
                        null,sortOrder);
                break;
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //  Implement insert new color and return Uri with ID
        Uri returnUri = null;
        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {

            case CARD:
                long id = db.insert(TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                break;

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return  returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
            @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();
        int deletedRows = 0;
        switch (sUriMatcher.match(uri)){
            case CARD_WITH_ID:
                String id = uri.getLastPathSegment();
                String selectionString = Contract.Columns._ID +" = ?";
                String[] selectionArgsArray = new String[]{id};

                deletedRows = db.delete(TABLE_NAME,selectionString,selectionArgsArray);
                break;

        }

        if (deletedRows != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
            @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("This provider does not support updates");
    }

    /**
     * Database contract
     */
    public static class Contract {
        public static final String TABLE_NAME = "cards";
        public static final String CONTENT_AUTHORITY = "com.google.developer.colorvalue";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
                .authority(CONTENT_AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        public static final class Columns implements BaseColumns {
            public static final String COLOR_HEX = "question";
            public static final String COLOR_NAME = "answer";
        }
    }

}
