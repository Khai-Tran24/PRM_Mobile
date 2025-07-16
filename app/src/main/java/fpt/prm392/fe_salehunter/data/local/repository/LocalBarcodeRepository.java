package fpt.prm392.fe_salehunter.data.local.repository;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.prm392.fe_salehunter.data.local.dao.BarcodeDao;
import fpt.prm392.fe_salehunter.data.local.database.SaleHunterDatabase;
import fpt.prm392.fe_salehunter.data.local.entity.BarcodeEntity;

/**
 * Repository for local database operations
 * Handles all barcode cache operations with background thread execution
 */
public class LocalBarcodeRepository {
    
    private final BarcodeDao barcodeDao;
    private final ExecutorService executorService;
    
    // Cache cleanup constants
    private static final long CACHE_RETENTION_DAYS = 30;
    private static final long CACHE_RETENTION_MS = CACHE_RETENTION_DAYS * 24 * 60 * 60 * 1000L;
    
    public LocalBarcodeRepository(Context context) {
        SaleHunterDatabase database = SaleHunterDatabase.getInstance(context);
        barcodeDao = database.barcodeDao();
        executorService = Executors.newFixedThreadPool(2);
    }
    
    /**
     * Interface for async callback operations
     */
    public interface BarcodeCallback<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }
    
    // Async operations for better performance
    
    /**
     * Lookup product name by barcode (async)
     */
    public void lookupProductByBarcode(String barcode, BarcodeCallback<String> callback) {
        executorService.execute(() -> {
            try {
                String productName = barcodeDao.getProductNameByBarcode(barcode);
                callback.onSuccess(productName);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Cache a barcode lookup result (async)
     */
    public void cacheBarcodeLookup(String barcode, String productName, String brand, String source, BarcodeCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                BarcodeEntity entity = new BarcodeEntity(barcode, productName, source);
                entity.setBrand(brand);
                long id = barcodeDao.insertBarcode(entity);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Get all cached barcodes (async)
     */
    public void getAllBarcodes(BarcodeCallback<List<BarcodeEntity>> callback) {
        executorService.execute(() -> {
            try {
                List<BarcodeEntity> barcodes = barcodeDao.getAllBarcodes();
                callback.onSuccess(barcodes);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Search barcodes by product name (async)
     */
    public void searchByProductName(String productName, BarcodeCallback<List<BarcodeEntity>> callback) {
        executorService.execute(() -> {
            try {
                List<BarcodeEntity> barcodes = barcodeDao.searchByProductName(productName);
                callback.onSuccess(barcodes);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Get recent barcode lookups (async)
     */
    public void getRecentBarcodes(int limit, BarcodeCallback<List<BarcodeEntity>> callback) {
        executorService.execute(() -> {
            try {
                List<BarcodeEntity> barcodes = barcodeDao.getRecentBarcodes(limit);
                callback.onSuccess(barcodes);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Clean up old cache entries (async)
     */
    public void cleanupOldCache(BarcodeCallback<Integer> callback) {
        executorService.execute(() -> {
            try {
                long cutoffTime = System.currentTimeMillis() - CACHE_RETENTION_MS;
                List<BarcodeEntity> oldBarcodes = barcodeDao.getOldBarcodes(cutoffTime);
                int deletedCount = oldBarcodes.size();
                barcodeDao.deleteOldBarcodes(cutoffTime);
                callback.onSuccess(deletedCount);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Get cache statistics (async)
     */
    public void getCacheStats(BarcodeCallback<CacheStats> callback) {
        executorService.execute(() -> {
            try {
                int totalCount = barcodeDao.getBarcodesCount();
                long cutoffTime = System.currentTimeMillis() - CACHE_RETENTION_MS;
                List<BarcodeEntity> oldBarcodes = barcodeDao.getOldBarcodes(cutoffTime);
                int oldCount = oldBarcodes.size();
                
                CacheStats stats = new CacheStats(totalCount, oldCount, totalCount - oldCount);
                callback.onSuccess(stats);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Clear all cache (async)
     */
    public void clearAllCache(BarcodeCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                barcodeDao.deleteAllBarcodes();
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    /**
     * Data class for cache statistics
     */
    public static class CacheStats {
        public final int totalEntries;
        public final int oldEntries;
        public final int activeEntries;
        
        public CacheStats(int totalEntries, int oldEntries, int activeEntries) {
            this.totalEntries = totalEntries;
            this.oldEntries = oldEntries;
            this.activeEntries = activeEntries;
        }
    }
    
    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
