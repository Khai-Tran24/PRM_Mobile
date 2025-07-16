package fpt.prm392.fe_salehunter.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Modern barcode lookup entity for offline product identification
 */
@Entity(
    tableName = "barcode_cache",
    indices = {@Index(value = "barcode", unique = true)}
)
public class BarcodeEntity {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    
    @ColumnInfo(name = "barcode")
    private String barcode;
    
    @ColumnInfo(name = "product_name")
    private String productName;
    
    @ColumnInfo(name = "brand")
    private String brand;
    
    @ColumnInfo(name = "category")
    private String category;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;
    
    @ColumnInfo(name = "updated_at")
    private long updatedAt;
    
    @ColumnInfo(name = "source")
    private String source; // "upc_item_db", "barcode_monster", "manual"
    
    // Constructors
    public BarcodeEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    @Ignore
    public BarcodeEntity(String barcode, String productName, String source) {
        this();
        this.barcode = barcode;
        this.productName = productName;
        this.source = source;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}
