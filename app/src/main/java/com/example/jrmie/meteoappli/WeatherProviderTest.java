package com.example.jrmie.meteoappli;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by Jérémie Décome on 05/11/2016.
 * Classe de test pour tester le ContentProvider de l'application
 */
public class WeatherProviderTest extends ApplicationTestCase<Application> {
    public WeatherProviderTest() {
        super(Application.class);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        Application app = getApplication();
    }
}
