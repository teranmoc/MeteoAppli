package com.example.jrmie.meteoappli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Jérémie Décome on 19/09/2016.
 */
public class AddCityActivity extends AppCompatActivity {
    EditText editCity;
    Spinner countriesList;
    String iso;     // code ISO du pays
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city);

        this.editCity = (EditText) findViewById(R.id.editCity);
        this.countriesList = (Spinner) findViewById(R.id.countriesList);
        this.countriesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String values [] =  getResources().getStringArray(R.array.countriesValues);
                AddCityActivity.this.iso = values[arg2];
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_city, menu);
        return true;
    }



    //gère le clic sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_city:     // Sauvegarde d'une ville
                this.save();
                return true;
            case R.id.back_main:
                Intent i = new Intent(getApplicationContext(), AddCityActivity.class);
                setResult(1, i);
                AddCityActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void save() {
        String name = this.editCity.getText().toString();
        String country = this.countriesList.getSelectedItem().toString();
        Log.v("DEBUG", "Code pays avant save : " + this.iso);
        if(!name.isEmpty() && !this.iso.isEmpty()) {
            Intent i;
            i = new Intent(getApplicationContext(), AddCityActivity.class);
            i.putExtra("name", name);
            i.putExtra("country", country);
            i.putExtra("iso", this.iso);
            setResult(2, i);
            Toast.makeText(AddCityActivity.this, "Cette ville a été ajoutée", Toast.LENGTH_SHORT).show();
            AddCityActivity.this.finish();
        }
        else {  // gestion des erreurs
            Toast.makeText(AddCityActivity.this, "Merci de renseigner le nom de la nouvelle ville et le pays si vous désirez l'enregistrer", Toast.LENGTH_SHORT).show();
        }
    }
}
