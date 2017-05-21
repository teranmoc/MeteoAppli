package com.example.jrmie.meteoappli;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TP2 - Application Météo
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView cities;
    private ArrayList<City> list;
    private CityManager cm;
    private CursorAdapter ca;
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.cities = (ListView) findViewById(R.id.listViewCities);
        getLoaderManager().initLoader(LOADER_ID, null, this);


        this.cm = new CityManager(this);
        this.cm.open();         // ouverture de la base de données

        // lecture des préférences stockées dans un SharedPreferences
        SharedPreferences sp = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        if(!sp.contains("windUnit") || !sp.contains("windDirectionUnit") || !sp.contains("tempUnit")) {  // s'ils n'existent pas, on en crée par défaut
            sp.edit()
                    .putInt("windUnit", 0)              // 0 : unité de vitesse de vent en km/h
                    .putInt("windDirectionUnit", 1)     // 1 : unité de direction du vent en degré
                    .putInt("tempUnit", 0)              // 0 : unité de température en degré Celcius
                    .apply();
        }

        // Test du content provider
        /*Cursor c = getContentResolver().query(Uri.parse("content://com.example.jrmie.meteoappli/weather"), null, null, null, null);
        if(c != null) {
            Log.v("DEBUG", "le curseur n'est pas null !!");
        }//*/


        this.list = this.cm.getCitiesList();     // lecture de la base de données via le CityManager
        this.refreshListView();
        AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {     // tâche de mise à jour des villes à l'ouverture
                try {
                    int t = MainActivity.this.list.size();
                    for(int i = 0; i < t; i++) {
                        MainActivity.this.refreshCity(i);
                    }
                }
                catch(Exception e) {
                    Toast.makeText(MainActivity.this, "Erreur lors de la mise à jour à l'ouverture. Merci de retenter dans quelques instants. ", Toast.LENGTH_SHORT).show();
                    Log.v("DEBUG", "Catch - Mise à jour à l'ouverture\ne : " + e.toString());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Toast.makeText(MainActivity.this, "La météo de chaque ville a été mise à jour", Toast.LENGTH_SHORT).show();
            }
        };
        t.execute();
        this.refreshListView();

        // affichage de la météo de la ville sélectionnée
        this.cities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, CityViewActivity.class);
                City c = MainActivity.this.list.get(position);      // lecture de la position et extraction de l'objet concerné
                i.putExtra("city", c);
                i.putExtra("position", position);
                startActivityForResult(i, 1);
            }
        });

        // affichage de la boite de dialogue de demande de suppression
        this.cities.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, final int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Suppression d'une ville");
                alertDialog.setMessage("Désirez vous supprimer cette ville de l'application ?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Supprimer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                City c = MainActivity.this.list.get(position);

                                int v = MainActivity.this.cm.deleteCity(c); // suppression dans la BDD
                                if(v > 0) {         // suppression dans la list view
                                    MainActivity.this.list.remove(position);
                                    MainActivity.this.refreshListView();     // refresh de la listview
                                    Toast.makeText(MainActivity.this, "La ville " + c.getName() + " a été supprimée de l'application", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }


                            }
                        }
                );
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                alertDialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //gère le clic sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()){
            case R.id.plus:     // Ajout d'une ville => démarrage de l'activité d'ajout
                i = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(i, 2);
                return true;
            case R.id.refresh:  // Rafraichissement de toutes les villes via le WS
                AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
                    boolean refresh = true;
                    @Override
                    protected Void doInBackground(Void... params) {
                        publishProgress(params);
                        try {
                            int t = MainActivity.this.list.size();
                            for(int i = 0; i < t; i++) {
                                refresh = MainActivity.this.refreshCity(i);
                            }
                        }
                        catch(Exception e) {
                            Log.v("DEBUG", "Exception - Mise à jour via le bouton\ne : " + e.toString());
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        if(this.refresh) {
                            Toast.makeText(MainActivity.this, "La météo de chaque ville a été mise à jour", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Pas de connectivité, les données chargées peuvent être vielle. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                t.execute();
                this.refreshListView();     // refresh de la listview

                return true;
            case R.id.setting:  // Appel de la fenêtre de configuration de l'application
                i = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(i, 4);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Traitement du retour des activités
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2) {   // le bouton Sauvegarde a été actionné et les deux champs sont renseignés
            City newCity = new City(data.getStringExtra("name"), data.getStringExtra("country"), data.getStringExtra("iso"));
            MainActivity.this.cm.addCity(newCity);      // on ajoute la ville dans la base de données
            this.list.add(newCity);                     // puis sauvegarde dans l'arraylist
            AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        MainActivity.this.refreshCity(MainActivity.this.list.size() - 1);   // l'élément étant le dernier ajouté, son index est donc le dernier de la liste
                    }
                    catch(Exception e) {
                        Log.v("DEBUG", "Ajout ville exception\ne : " + e.toString());
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            t.execute();
            this.refreshListView();     // refresh de la listview
        }
        else if(resultCode == 3) {  // le bouton Retour de la vue d'une ville a été actionné et les données ont été mises à jour, on rafraichit les données qui était présente
            City receive = (City) data.getSerializableExtra("city");
            if(receive != null) {   // sauvegarde de l'objet modifié dans l'arraylist + en BDD
                this.list.set(data.getIntExtra("position", 0), receive);        // stockage dans l'arraylist
                this.cm.updateCity(receive);                                    // mise à jour de la ville en base de données
            }
        }
        else {
            Log.v("DEBUG", "resultCode : " + resultCode);
        }
        this.refreshListView();     // refresh de la listview
    }

    /**
     * Rafraichit la listview affichée. Pour cela, la méthode utilise l'état actuel de l'arraylist this.cities
     * Il est donc nécessaire de la modifier avant de rafraichir la listview
     */
    private void refreshListView()
    {
        ArrayAdapter<City> adc = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, this.list);
        this.cities.setAdapter(adc);
    }

    /**
     * Rafraichit l'objet ville et en BDD indiqué par sa position dans sa liste.
     * Méthode à optimiser et refactoriser !
     * Soulève une exception si erreur
     * @param position
     */
    private boolean refreshCity(int position) throws Exception {
        City cityList = MainActivity.this.list.get(position);

        ConnectivityManager cnm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cnm.getActiveNetworkInfo();
        if((ni != null) && (ni.isConnected())) {        // il y a une connectivité, on affiche les données du webservice et on met à jour la BDD
            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                    cityList.getName().replace(" ", "%20") + "%2C%20" +
                    cityList.getIso() + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
            InputStream is = new URL(url).openStream();
            // lecture de l'objet JSON
            JSONResponseHandler js = new JSONResponseHandler();
            List<String> l = js.handleResponse(is, "UTF-8");

            // traitement du vent
            String[] wind = l.get(0).split(" ");
            float ws = Float.parseFloat(wind[0]);
            String wd = wind[1].substring(1, wind[1].length() - 1);
            MainActivity.this.list.get(position).setWindSpeed(ws);
            MainActivity.this.list.get(position).setWindDirection(wd);
            MainActivity.this.list.get(position).setOutsiteTemp(Integer.parseInt(l.get(1)));    // température
            MainActivity.this.list.get(position).setPressure(Float.parseFloat(l.get(2)));       // pression atmosphérique

            // date et heure de mise à jour
            long ts = System.currentTimeMillis();
            MainActivity.this.list.get(position).setLastUpdate(ts);

            MainActivity.this.cm.updateCity(MainActivity.this.list.get(position));              // on met à jour la ville dans la base de données
            return true;
        }
        else {          // Pas de connectivité, on remonte les dernières valeurs de la base de données
            City c = MainActivity.this.cm.getCityByNameAndIso(cityList.getName(), cityList.getIso());   // cityList contient d'anciennes valeurs et est présent dans la liste

            // traitement du vent
            MainActivity.this.list.get(position).setWindSpeed(c.getWindSpeed());
            MainActivity.this.list.get(position).setWindDirection(c.getWindDirection());
            MainActivity.this.list.get(position).setOutsiteTemp(c.getOutsiteTemp());    // température
            MainActivity.this.list.get(position).setPressure(c.getPressure());          // pression atmosphérique

            // date et heure de mise à jour
            String ts = c.getLastUpdate(false);
            MainActivity.this.list.get(position).setLastUpdate(Long.valueOf(ts));
            return false;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    // Partie Question 4 - Utilisation d'un chargeur
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri u = Uri.parse("content://com.example.jrmie.meteoappli/weather");
        return new CursorLoader(MainActivity.this, u, new String[] {"city_name"}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case LOADER_ID:
                if(data != null) {
                    this.ca.swapCursor(data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.ca.swapCursor(null);

    }

}