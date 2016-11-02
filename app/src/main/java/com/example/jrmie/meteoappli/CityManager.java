package com.example.jrmie.meteoappli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jérémie on 12/10/2016.
 * Permet d'encapsuler et de gérer, côté application, la base de données pour les villes
 */
public class CityManager {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "citiesDb.db";

    private static final String TABLE_NAME = "cities";
    private static final String PRIMARY_KEY = "city_ref";
    private static final String CITY_NAME = "city_name";
    private static final int COL_UPDATE_DATE = 4;
    private static final int COL_TEMPERATURE = 5;
    private static final int COL_WIND_SPEED = 6;
    private static final int COL_WIND_DIRECTION = 7;
    private static final int COL_PRESSURE = 8;


    private SQLiteDatabase sqlBdd;

    private Database bdd;

    public CityManager(Context context) {
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

    /**
     * Retourne la liste des villes présentes dans la base de données => fonction getAllCities() du TP
     * @return
     */
    public ArrayList<City> getCitiesList() {
        ArrayList<City> ret = new ArrayList<City>();
        Cursor c = this.sqlBdd.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(c.moveToFirst()) {
            while(c.isAfterLast() == false) {
                Log.v("DEBUG", "==> : " + c.getString(3));
                City city = new City(c.getString(1), c.getString(2), c.getString(3));
                city.setLastUpdate(c.getLong(COL_UPDATE_DATE));
                city.setOutsiteTemp(c.getInt(COL_TEMPERATURE));
                city.setWindSpeed((float) c.getInt(COL_WIND_SPEED));
                city.setWindDirection(c.getString(COL_WIND_DIRECTION));
                city.setPressure((float) c.getInt(COL_PRESSURE));
                ret.add(city);
                c.moveToNext();
            }
        }
        return ret;
    }
    public void resetDatabase() {
        this.bdd.onUpgrade(this.sqlBdd, 1, 1);
    }
    public City getCityByReference(int ref) {
        City c = new City("Paris", "France", "FR");
        return c;
    }
    public long addCity(City c) {
        ContentValues v = new ContentValues();
        v.put(CITY_NAME, c.getName());
        v.put("country", c.getCountry());
        v.put("iso", c.getIso());
        return this.sqlBdd.insert(TABLE_NAME, null, v);
    }
    public int updateCity(City c) {
        Log.v("DEBUG", "[Update City] Mise à jour de la ville " + c.getName());
        ContentValues v = new ContentValues();
        v.put(CITY_NAME, c.getName());
        v.put("country", c.getCountry());
        long ts = System.currentTimeMillis() / 1000;
        v.put("update_date", ts);
        v.put("temperature", c.getOutsiteTemp());
        v.put("windSpeed", c.getWindSpeed());
        v.put("windDirection", c.getWindDirection());
        v.put("pressure", c.getPressure());
        String where = CITY_NAME + " = \"" + c.getName() + "\" AND country = \"" + c.getCountry() + "\"";
        return this.sqlBdd.update(TABLE_NAME, v, where, null);

    }
    public int deleteCity(City c) {
        String where = CITY_NAME + " = \"" + c.getName() + "\" AND country = \"" + c.getCountry() + "\"";
        //Log.v("DEBUG", "Clause where : " + where);
        return this.sqlBdd.delete(TABLE_NAME, where, null);
    }
    private City cursorToCity(Cursor cr) {
        return null;
    }
}
