package com.example.jrmie.meteoappli;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jérémie on 12/10/2016.
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
    private static final String CREATE_TABLE_2 = "CREATE TABLE IF NOT EXISTS `settings` (" +
            "`variable` TEXT, " +
            "`value` TEXT, " +
            "PRIMARY KEY(`variable`)" +
            ");";
    public Database(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        //db.execSQL(CREATE_TABLE_2);
        //db.execSQL("INSERT INTO settings(variable, value) VALUES('windUnit', 'km/h'), ('windDirectionUnit', 'degree'), ('tempUnit', 'celcius');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DEBUG", "Reset BDD");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        //db.execSQL("DROP TABLE IF EXISTS settings;");
        onCreate(db);
    }
}
