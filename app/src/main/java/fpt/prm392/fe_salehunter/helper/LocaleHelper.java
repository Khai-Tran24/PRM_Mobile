package fpt.prm392.fe_salehunter.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import fpt.prm392.fe_salehunter.util.AppSettingsManager;

/**
 * Helper class for handling locale and configuration changes
 */
public class LocaleHelper {
    
    /**
     * Apply locale configuration to context
     */
    public static Context applyLocaleToContext(Context context) {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(context)) {
            language = Locale.getDefault().getLanguage();
        } else {
            language = AppSettingsManager.getLanguageKey(context);
        }

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        context.getResources().getConfiguration().setLocale(locale);
        context.getResources().getConfiguration().setLayoutDirection(locale);
        
        return context;
    }
    
    /**
     * Update locale configuration for current context
     */
    public static void updateLocaleConfiguration(Context context) {
        String language;
        if (AppSettingsManager.isLanguageSystemDefault(context)) {
            language = Locale.getDefault().getLanguage();
        } else {
            language = AppSettingsManager.getLanguageKey(context);
        }

        Locale locale = new Locale(language);
        Configuration config = context.getResources().getConfiguration();
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
