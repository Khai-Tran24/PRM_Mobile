package fpt.prm392.fe_salehunter.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import fpt.prm392.fe_salehunter.data.local.dao.BarcodeDao;
import fpt.prm392.fe_salehunter.data.local.entity.BarcodeEntity;

/**
 * Modern Room database implementation for SaleHunter app
 */
@Database(
    entities = {BarcodeEntity.class},
    version = 3,
    exportSchema = false
)
public abstract class SaleHunterDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "salehunter_db";
    private static volatile SaleHunterDatabase INSTANCE;
    
    // Abstract DAO methods
    public abstract BarcodeDao barcodeDao();
    
    // Migration from version 2 (old bc table) to version 3 (new barcode_cache table)
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create new barcode_cache table
            database.execSQL("CREATE TABLE IF NOT EXISTS `barcode_cache` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`barcode` TEXT, " +
                    "`product_name` TEXT, " +
                    "`brand` TEXT, " +
                    "`category` TEXT, " +
                    "`created_at` INTEGER NOT NULL, " +
                    "`updated_at` INTEGER NOT NULL, " +
                    "`source` TEXT)");
            
            // Create unique index on barcode
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_barcode_cache_barcode` " +
                    "ON `barcode_cache` (`barcode`)");
            
            // Migrate data from old bc table to new barcode_cache table if it exists
            database.execSQL("INSERT OR IGNORE INTO barcode_cache (barcode, product_name, created_at, updated_at, source) " +
                    "SELECT barcode, product, " +
                    "datetime('now') * 1000, datetime('now') * 1000, 'legacy' " +
                    "FROM bc WHERE bc.barcode IS NOT NULL AND bc.product IS NOT NULL");
            
            // Drop old bc table
            database.execSQL("DROP TABLE IF EXISTS bc");
        }
    };
    
    /**
     * Get database instance with singleton pattern
     */
    public static SaleHunterDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SaleHunterDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SaleHunterDatabase.class,
                            DATABASE_NAME
                    )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // Only as last resort
                    .build(); // Remove allowMainThreadQueries for better performance
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Close database instance
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
