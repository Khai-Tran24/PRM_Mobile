package fpt.prm392.fe_salehunter.util;

import fpt.prm392.fe_salehunter.BuildConfig;

public class BuildConfigManager {
    
    /**
     * Get Maps API key from BuildConfig
     * Note: For MapLibre with OpenStreetMap, API key is typically not required
     * but may be needed for premium tile services
     */
    public static String getMapsApiKey() {
        return BuildConfig.MAPS_API_KEY;
    }
    
    /**
     * Check if we're in debug mode
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
    
    /**
     * Get application ID
     */
    public static String getApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }
}
