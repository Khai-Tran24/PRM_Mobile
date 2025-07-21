package fpt.prm392.fe_salehunter.util;

import android.content.Context;

import org.maplibre.android.MapLibre;

/**
 * Manager class for MapLibre initialization and configuration
 */
public class MapLibreManager {
    private static boolean isInitialized = false;

    /**
     * Initialize MapLibre with the application context.
     * Should be called once in Application class or before first MapView usage.
     */
    public static void initialize(Context context) {
        if (!isInitialized) {
            // Initialize MapLibre with application context
            MapLibre.getInstance(context.getApplicationContext());
            isInitialized = true;
        }
    }

    /**
     * Get the default OpenStreetMap style URL
     * Using MapLibre's demo tiles - replace with your own tile server if needed
     */
    public static String getDefaultStyleUrl() {
        return "https://demotiles.maplibre.org/style.json";
    }

    /**
     * Alternative style URLs for different map styles
     */
    public static class StyleUrls {
        public static final String MAPLIBRE_DEMO = "https://demotiles.maplibre.org/style.json";
        public static final String OSM_BRIGHT = "https://tiles.stadiamaps.com/styles/osm_bright.json";
        public static final String MAPTILER_BASIC = "https://api.maptiler.com/maps/basic/style.json";
        public static final String SATELLITE = "https://api.maptiler.com/maps/satellite/style.json";
        
        // You can add your own tile server URLs here
        // For production, consider using your own tile server
    }
    
    /**
     * Get satellite style URL (requires API key for production use)
     */
    public static String getSatelliteStyleUrl() {
        // For demo purposes, use a basic satellite style
        // In production, you should use your own MapTiler API key
        return "https://api.maptiler.com/maps/hybrid/style.json?key=get_your_own_OpIi9ZULNHzrESv6T2vL";
    }

    /**
     * Check if MapLibre is initialized
     */
    public static boolean isInitialized() {
        return isInitialized;
    }
}
