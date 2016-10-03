package com.example.jrmie.meteoappli;

import android.os.Parcelable;
import android.text.Html;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;
/**
 * Created by Jérémie Décome on 17/09/2016.
 */
public class City implements Serializable {
    private String name;
    private String country;
    private String lastUpdate;
    private float windSpeed;
    private String windDirection;      // direction du vent en degré ?
    private float pressure;
    private int outsiteTemp;        // température extérieur
    public City(String name, String country) {
        this.name = name;
        this.country = country;
        this.lastUpdate = "";
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
    public String getLastUpdate() {
        return this.lastUpdate;
    }
    public void setLastUpdate(String lastUpdate) {
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
}
