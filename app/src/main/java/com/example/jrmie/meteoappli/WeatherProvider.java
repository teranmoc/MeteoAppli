package com.example.jrmie.meteoappli;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Jérémie Décome on 02/11/2016.
 */
public class WeatherProvider extends ContentProvider {
    private static final String PROVIDER_NAME = "com.example.jrmie.meteoappli";
    private static final int DIR = 0;
    private static final int ITEM = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private Database db = null;
    static {
        sUriMatcher.addURI(PROVIDER_NAME, "weather", DIR);       // correspond à content://com.example.jrmie.meteoappli/weather
        sUriMatcher.addURI(PROVIDER_NAME, "weather/*/*", ITEM);  // correspond à content://com.example.jrmie.meteoappli/weather/<country>/<city>
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case DIR:
                return "vnd.android.cursor.dir/vnd." + PROVIDER_NAME;
            case ITEM:
                return "vnd.android.cursor.item/vnd." + PROVIDER_NAME;
            default:
                break;
        }
        return "";
    }
    @Override
    public boolean onCreate() {
        db = new Database(getContext(), "citiesDb.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v("DEBUG", "query appelé");
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("cities");
        Cursor c = builder.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.getWritableDatabase().insert(db.getTableName(), db.getPrimaryKey(), values);
        if(id > -1) {
            Uri u = ContentUris.withAppendedId(Uri.parse("content://" + PROVIDER_NAME + "/" + db.getTableName()), id);
            getContext().getContentResolver().notifyChange(u, null);
            return u;
        }
        else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int c = db.getWritableDatabase().delete(db.getTableName(), selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long id = this.getId(uri);
        if(id < 0) {
            return db.getWritableDatabase().update(db.getTableName(), values, selection, selectionArgs);
        }
        return -1;
    }

    private long getId(Uri uri) {
        Log.v("DEBUG", "getId appelé");
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            try {
                return Long.parseLong(lastPathSegment);
            } catch (NumberFormatException e) {
                Log.v("DEBUG", "Exception getID : " + e);
            }
        }
        return -1;
    }
}
