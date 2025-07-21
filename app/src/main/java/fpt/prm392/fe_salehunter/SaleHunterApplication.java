package fpt.prm392.fe_salehunter;

import android.app.Application;

import fpt.prm392.fe_salehunter.util.MapLibreManager;

/**
 * Custom Application class for SaleHunter app initialization
 */
public class SaleHunterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize MapLibre for OpenStreetMap support
        MapLibreManager.initialize(this);
    }
}
