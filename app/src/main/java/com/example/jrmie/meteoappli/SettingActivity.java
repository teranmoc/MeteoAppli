package com.example.jrmie.meteoappli;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Jérémie Décome on 26/10/2016.
 */
public class SettingActivity extends AppCompatActivity {
    Spinner windUnit, windDirectionUnit, tempUnit;
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);

        this.windUnit = (Spinner) findViewById(R.id.windUnit);
        this.windDirectionUnit = (Spinner) findViewById(R.id.windDirectionUnit);
        this.tempUnit = (Spinner) findViewById(R.id.tempUnit);

        // lecture des préférences utilisateurs
        this.sp = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        this.windUnit.setSelection(this.sp.getInt("windUnit", -1));
        this.windDirectionUnit.setSelection(this.sp.getInt("windDirectionUnit", -1));
        this.tempUnit.setSelection(this.sp.getInt("tempUnit", -1));//*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_city, menu);      // la configuration utilise le même menu que l'ajout d'une ville
        return true;
    }
    //gère le clic sur une action de l'ActionBar
    // l'ActionBar étant la même que pour l'ajout d'une ville, il est normal que le bouton Sauvegarder ait le même nom que l'ajout d'une ville !
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_city:     // Sauvegarde
                // sauvegarde des unités
                this.sp.edit()
                        .putInt("windUnit", this.windUnit.getSelectedItemPosition())
                        .putInt("windDirectionUnit", this.windDirectionUnit.getSelectedItemPosition())
                        .putInt("tempUnit", this.tempUnit.getSelectedItemPosition())
                        .apply();
                Toast.makeText(this, "La nouvelle configuration a bien été sauvegardée. ", Toast.LENGTH_SHORT).show();
                SettingActivity.this.finish();
                return true;
            case R.id.back_main:
                SettingActivity.this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
