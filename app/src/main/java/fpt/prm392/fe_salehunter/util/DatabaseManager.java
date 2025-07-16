package fpt.prm392.fe_salehunter.util;

import android.content.Context;

import fpt.prm392.fe_salehunter.data.local.database.SaleHunterDatabase;
import fpt.prm392.fe_salehunter.data.local.repository.LocalBarcodeRepository;

/**
 * Database manager for app-wide database operations
 * Provides centralized access to database components
 */
public class DatabaseManager {
    
    private static DatabaseManager instance;
    private SaleHunterDatabase database;
    private LocalBarcodeRepository barcodeRepository;
    
    private DatabaseManager(Context context) {
        database = SaleHunterDatabase.getInstance(context.getApplicationContext());
        barcodeRepository = new LocalBarcodeRepository(context.getApplicationContext());
    }
    
    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }
    
    public SaleHunterDatabase getDatabase() {
        return database;
    }
    
    public LocalBarcodeRepository getBarcodeRepository() {
        return barcodeRepository;
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        if (barcodeRepository != null) {
            barcodeRepository.shutdown();
        }
        SaleHunterDatabase.closeDatabase();
        instance = null;
    }
}
