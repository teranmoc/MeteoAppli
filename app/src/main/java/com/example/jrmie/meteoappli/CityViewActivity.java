package com.example.jrmie.meteoappli;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jérémie Décome on 18/09/2016.
 */
public class CityViewActivity extends AppCompatActivity {
    TextView textCity;
    TextView textCountry;
    TextView textWind;
    TextView textTemp;
    TextView textPressure;
    TextView textMaj;
    City receive;
    int position;
    boolean updated = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_view);

        this.textCity = (TextView) findViewById(R.id.textCity);
        this.textCountry = (TextView) findViewById(R.id.textCountry);
        this.textWind = (TextView) findViewById(R.id.textWind);
        this.textTemp = (TextView) findViewById(R.id.textTemp);
        this.textPressure = (TextView) findViewById(R.id.textPressure);
        this.textMaj = (TextView) findViewById(R.id.textMaj);

        Intent r = getIntent();
        this.receive = (City) r.getSerializableExtra("city");
        this.position = r.getIntExtra("position", 0);
        if(this.receive != null) {
            this.setValuesInterface();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_view, menu);
        return true;
    }
    //gère le clic sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.back_main:     // Retour
                Intent i = new Intent(getApplicationContext(), CityViewActivity.class);
                if(this.updated) {      // vu qu'il y a eu MAJ des données, on renvoi l'objet à l'activité principale
                    i.putExtra("city", this.receive);
                    i.putExtra("position", this.position);
                    setResult(3, i);
                }
                CityViewActivity.this.finish();
                return true;
            case R.id.refresh_city: // appel webservice pour mise à jour de cette ville, et insertion dans l'objet
                AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                                    CityViewActivity.this.receive.getName().replace(" ", "%20") + "%2C%20" +
                                    CityViewActivity.this.receive.getCountry().replace(" ", "%20") + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

                            InputStream is = new URL(url).openStream();

                            // lecture de l'objet JSON
                            JSONResponseHandler js = new JSONResponseHandler();
                            List<String> l = js.handleResponse(is, "UTF-8");

                            // traitement du vent
                            String[] wind = l.get(0).split(" ");
                            CityViewActivity.this.receive.setWindSpeed(Float.parseFloat(wind[0]));
                            CityViewActivity.this.receive.setWindDirection(wind[1].substring(1, wind[1].length() - 1));

                            // température
                            CityViewActivity.this.receive.setOutsiteTemp(Integer.parseInt(l.get(1)));

                            // pression
                            CityViewActivity.this.receive.setPressure(Float.parseFloat(l.get(2)));

                            // date et heure de mise à jour
                            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
                            String now = f.format(new Date());
                            CityViewActivity.this.receive.setLastUpdate(now);
                        }
                        catch(Exception e) {
                            Log.v("DEBUG", "Catch city view refresh : \ne : " + e.toString());
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        Toast.makeText(CityViewActivity.this, "La météo de la ville " + CityViewActivity.this.receive.getName() + " a été mise à jour", Toast.LENGTH_SHORT).show();
                        CityViewActivity.this.setValuesInterface();     // mise à jour de l'interface
                    }
                };
                t.execute();
                this.updated = true;        // passage à true du flag mis à jour
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Met à jour les valeurs sur le layout de la visualisation d'une ville
     */
    private void setValuesInterface() {
        CityViewActivity.this.textCity.setText(CityViewActivity.this.receive.getName());
        CityViewActivity.this.textCountry.setText(CityViewActivity.this.receive.getCountry());
        CityViewActivity.this.textWind.setText(CityViewActivity.this.receive.getWindSpeed() + " km/h (" +
                CityViewActivity.this.receive.getWindDirection() + ")");
        CityViewActivity.this.textTemp.setText(CityViewActivity.this.receive.getOutsiteTemp() + " °C");
        CityViewActivity.this.textPressure.setText(CityViewActivity.this.receive.getPressure() + " hPa");
        CityViewActivity.this.textMaj.setText(CityViewActivity.this.receive.getLastUpdate().toString());
    }
}
