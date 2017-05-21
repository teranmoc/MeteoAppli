package com.example.jrmie.meteoappli;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jérémie Décome on 12/10/2016.
 * Interaction avec la base de donées SQLite
 */
public class Database extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "cities";
    private static final String PRIMARY_KEY = "city_ref";
    private static final String CITY_NAME = "city_name";

    private static final String CREATE_TABLE_1 = "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (" +
            "`" + PRIMARY_KEY + "` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`" + CITY_NAME + "` TEXT, " +
            "`country` TEXT, " +
            "`iso` TEXT, " +
            "`update_date` INTEGER, " +
            "`temperature` REAL," +
            "`windSpeed` REAL, " +
            "`windDirection` TEXT, " +
            "`pressure` REAL, " +
            "UNIQUE (`" + CITY_NAME + "`,  `country`)" +
            ");";
    public Database(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }
    public String getTableName()
    {
        return TABLE_NAME;
    }
    public String getPrimaryKey()
    {
        return PRIMARY_KEY;
    }
}
