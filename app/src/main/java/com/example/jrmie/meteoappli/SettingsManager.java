package com.example.jrmie.meteoappli;       // SettingsManager

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jérémie on 29/10/2016.
 * Permet d'encapsuler et de gérer, côté application, la base de données pour les paramètres
 */
public class SettingsManager {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "citiesDb.db";

    private static final String TABLE_NAME = "settings";
    private static final String PRIMARY_KEY = "variable";
    /*private static final String CITY_NAME = "city_name";
    private static final int COL_UPDATE_DATE = 4;
    private static final int COL_TEMPERATURE = 5;
    private static final int COL_WIND_SPEED = 6;
    private static final int COL_WIND_DIRECTION = 7;
    private static final int COL_PRESSURE = 8;//*/


    private SQLiteDatabase sqlBdd;

    private Database bdd;

    public SettingsManager(Context context) {
        this.bdd = new Database(context, NOM_BDD, null, VERSION_BDD);
    }
    public void open() {
        this.sqlBdd = bdd.getWritableDatabase();
    }
    public void close() {
        this.sqlBdd.close();
    }
    public SQLiteDatabase getBDD() {
        return this.sqlBdd;
    }


    public void resetDatabase() {
        this.bdd.onUpgrade(this.sqlBdd, 1, 1);
    }
    public String getSettingByVariable(String variable) {
        String where = PRIMARY_KEY + " = \"" + variable + "\"";
        Log.v("DEBUG", "clause where : " + where);
        Cursor c = this.sqlBdd.query(TABLE_NAME, new String[] {"value"}, PRIMARY_KEY + " = \"" + variable + "\"", null, null, null, null);
        if(c.getCount() == 0) {
            return null;
        }

        c.moveToFirst();
        return c.getString(0);//*/
    }
    public int setSetting(String variable, String value) {
        ContentValues v = new ContentValues();
        v.put("value", value);
        String where = PRIMARY_KEY + " = \"" + variable + "\"";
        return this.sqlBdd.update(TABLE_NAME, v, where, null);
    }

}

