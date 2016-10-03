package com.example.jrmie.meteoappli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView cities;
    ArrayList<City> list;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putStringArrayList("list", this.list);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.cities = (ListView) findViewById(R.id.listViewCities);

        // création de la liste
        this.list = new ArrayList<City>();
        this.list.add(new City("Chateaurenard", "France"));
        this.list.add(new City("Dubai", "Emirats Arabes Unis"));
        this.list.add(new City("Boston", "Etats Unis d'Amérique"));
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
                                // suppression dans la BDD

                                // suppression dans la list view
                                City c = MainActivity.this.list.get(position);
                                MainActivity.this.list.remove(position);
                                MainActivity.this.refreshListView();     // refresh de la listview
                                Toast.makeText(MainActivity.this, "La ville " + c.getName() + " a été supprimée de l'application", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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
        switch (item.getItemId()){
            case R.id.plus:     // Ajout d'une ville => démarrage de l'activité d'ajout
                Intent i = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(i, 2);
                return true;
            case R.id.refresh:  // Rafraichissement de toutes les villes via le WS
                AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        publishProgress(params);
                        try {
                            int t = MainActivity.this.list.size();
                            for(int i = 0; i < t; i++) {
                                MainActivity.this.refreshCity(i);
                            }
                        }
                        catch(Exception e) {
                            Log.v("DEBUG", "Mise à jour via le bouton\ne : " + e.toString());
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
                this.refreshListView();     // refresh de la listview

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Traitement du retour des activités
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2) {   // le bouton Sauvegarde a été actionné et les deux champs sont renseignés
            City newCity = new City(data.getStringExtra("name"), data.getStringExtra("country"));
            // sauvegarde dans la BDD

            this.list.add(newCity);     // puis sauvegarde dans l'arraylist
            AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        int last = MainActivity.this.list.size() - 1;    // l'élément étant le dernier ajouté, son index est donc le dernier de la liste
                        MainActivity.this.refreshCity(last);
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
                // stockage dans l'arraylist
                int pos = data.getIntExtra("position", 0);
                this.list.set(pos, receive);

                // sauvegarde en BDD
            }
        }
        this.refreshListView();     // refresh de la listview
    }

    /**
     * Rafraichit la listview affichée. Pour cela, la méthode utilise l'état actuel de l'arraylist this.cities
     * Il est donc nécessaire de la modifier avant de rafraichir la listview
     */
    private void refreshListView()
    {
        ArrayAdapter<City> adc =
                new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, this.list);

        this.cities.setAdapter(adc);
    }

    /**
     * Rafraichit l'objet ville indiqué par sa position dans sa liste.
     * Soulève une exception si erreur
     * @param position
     */
    private void refreshCity(int position) throws Exception {
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                MainActivity.this.list.get(position).getName().replace(" ", "%20") + "%2C%20" +
                MainActivity.this.list.get(position).getCountry().replace(" ", "%20") + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        InputStream is = new URL(url).openStream();
        // lecture de l'objet JSON
        JSONResponseHandler js = new JSONResponseHandler();
        List<String> l = js.handleResponse(is, "UTF-8");

        // traitement du vent
        String[] wind = l.get(0).split(" ");
        MainActivity.this.list.get(position).setWindSpeed(Float.parseFloat(wind[0]));
        MainActivity.this.list.get(position).setWindDirection(wind[1].substring(1, wind[1].length() - 1));

        // température
        MainActivity.this.list.get(position).setOutsiteTemp(Integer.parseInt(l.get(1)));

        // pression
        MainActivity.this.list.get(position).setPressure(Float.parseFloat(l.get(2)));

        // date et heure de mise à jour
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
        MainActivity.this.list.get(position).setLastUpdate(f.format(new Date()));
    }
}