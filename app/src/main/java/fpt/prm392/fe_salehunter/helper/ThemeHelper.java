package fpt.prm392.fe_salehunter.helper;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import fpt.prm392.fe_salehunter.util.AppSettingsManager;

/**
 * Helper class for managing app theme settings
 */
public class ThemeHelper {
    
    /**
     * Apply theme based on user settings
     */
    public static void applyTheme(Context context) {
        switch (AppSettingsManager.getTheme(context)) {
            case AppSettingsManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case AppSettingsManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
