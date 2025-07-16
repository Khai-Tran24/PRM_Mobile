package fpt.prm392.fe_salehunter.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.prm392.fe_salehunter.data.local.entity.BarcodeEntity;

/**
 * Modern DAO for barcode cache operations
 */
@Dao
public interface BarcodeDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBarcode(BarcodeEntity barcode);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBarcodes(List<BarcodeEntity> barcodes);
    
    // Update operations
    @Update
    void updateBarcode(BarcodeEntity barcode);
    
    // Delete operations
    @Delete
    void deleteBarcode(BarcodeEntity barcode);
    
    @Query("DELETE FROM barcode_cache WHERE id = :id")
    void deleteBarcodeById(long id);
    
    @Query("DELETE FROM barcode_cache WHERE barcode = :barcode")
    void deleteBarcodeByCode(String barcode);
    
    @Query("DELETE FROM barcode_cache")
    void deleteAllBarcodes();
    
    // Query operations
    @Query("SELECT * FROM barcode_cache WHERE barcode = :barcode LIMIT 1")
    BarcodeEntity findByBarcode(String barcode);
    
    @Query("SELECT product_name FROM barcode_cache WHERE barcode = :barcode LIMIT 1")
    String getProductNameByBarcode(String barcode);
    
    @Query("SELECT * FROM barcode_cache ORDER BY updated_at DESC")
    List<BarcodeEntity> getAllBarcodes();
    
    @Query("SELECT * FROM barcode_cache WHERE product_name LIKE '%' || :productName || '%'")
    List<BarcodeEntity> searchByProductName(String productName);
    
    @Query("SELECT * FROM barcode_cache WHERE source = :source ORDER BY updated_at DESC")
    List<BarcodeEntity> getBarcodesBySource(String source);
    
    @Query("SELECT COUNT(*) FROM barcode_cache")
    int getBarcodesCount();
    
    @Query("SELECT * FROM barcode_cache ORDER BY updated_at DESC LIMIT :limit")
    List<BarcodeEntity> getRecentBarcodes(int limit);
    
    // Cache management
    @Query("DELETE FROM barcode_cache WHERE updated_at < :timestamp")
    void deleteOldBarcodes(long timestamp);
    
    @Query("SELECT * FROM barcode_cache WHERE updated_at < :timestamp")
    List<BarcodeEntity> getOldBarcodes(long timestamp);
}
