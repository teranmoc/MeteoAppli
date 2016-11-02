package com.example.jrmie.meteoappli;

import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import java.text.SimpleDateFormat;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;
/**
 * Created by Jérémie Décome on 17/09/2016.
 */
public class City implements Serializable {
    private String name;
    private String country;
    private String iso;     // code ISO du pays pour la requête Yahoo
    private long lastUpdate;
    private float windSpeed;
    private String windDirection;      // direction du vent en degré ?
    private float pressure;
    private int outsiteTemp;        // température extérieur
    public City(String name, String country, String iso) {
        this.name = name;
        this.country = country;
        this.iso = iso;
        Long tsLong = System.currentTimeMillis() / 1000;
        this.lastUpdate = tsLong;
        this.windSpeed = 0;
        this.windDirection = "";
        this.pressure = 0;
        this.outsiteTemp = 0;        // température extérieur
    }
    @Override
    public String toString() {      // nécessaire pour l'affichage sur la listview
        return this.name + " (" + this.country + ")";
    }
    public String getName() {
        return this.name;
    }
    public String getCountry() {
        return this.country;
    }
    public String getIso() { return this.iso; }
    public String getLastUpdate() {
        try{
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
            Date netDate = (new Date(this.lastUpdate));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public float getWindSpeed() {
        return this.windSpeed;
    }
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
    public String getWindDirection() {
        return this.windDirection;
    }
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }
    public float getPressure() {
        return this.pressure;
    }
    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
    public int getOutsiteTemp() {
        return this.outsiteTemp;
    }
    public void setOutsiteTemp(int outsiteTemp) {
        this.outsiteTemp = outsiteTemp;
    }
    public String afficher() {
        String txt = this.name + " (" + this.country + ")\n";
        txt += "Température : " + this.outsiteTemp + " °C\n";
        txt += "Vent : " + this.windSpeed + " km/h (" + this.windDirection + ")\n";
        txt += "Pression : " + this.pressure + " hPa\n";
        txt += "Mise à jour le : " + this.lastUpdate;
        return txt;
    }
}
