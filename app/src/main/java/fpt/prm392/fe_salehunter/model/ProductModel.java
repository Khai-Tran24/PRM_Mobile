package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductModel {
    // Store type constants
    public static final String ONLINE_STORE = "online";
    public static final String LOCAL_STORE = "local";

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("currentPrice")
    private double currentPrice;

    @SerializedName("salePercent")
    private Integer salePercent;

    @SerializedName("finalPrice")
    private double finalPrice;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @SerializedName("images")
    private List<ProductImageModel> images;

    @SerializedName("mainImage")
    private String mainImage;

    @SerializedName("averageRating")
    private double averageRating;

    @SerializedName("ratingCount")
    private int ratingCount;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    @SerializedName("storeId")
    private long storeId;

    @SerializedName("storeName")
    private String storeName;

    @SerializedName("storeImageUrl")
    private String storeImageUrl;

    @SerializedName("storeLatitude") 
    private double storeLatitude;

    @SerializedName("storeLongitude")
    private double storeLongitude;

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("updatedDate")
    private String updatedDate;

    @SerializedName("isActive")
    private boolean isActive;

    public static class ProductImageModel {
        @SerializedName("id")
        private long id;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("isMainImage")
        private boolean isMainImage;

        @SerializedName("createdDate")
        private String createdDate;

        // Getters and setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public boolean isMainImage() { return isMainImage; }
        public void setMainImage(boolean mainImage) { isMainImage = mainImage; }
        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public Integer getSalePercent() { return salePercent; }
    public void setSalePercent(Integer salePercent) { this.salePercent = salePercent; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<ProductImageModel> getImages() { return images; }
    public void setImages(List<ProductImageModel> images) { this.images = images; }
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public long getStoreId() { return storeId; }
    public void setStoreId(long storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreImageUrl() { return storeImageUrl; }
    public void setStoreImageUrl(String storeImageUrl) { this.storeImageUrl = storeImageUrl; }
    public double getStoreLatitude() { return storeLatitude; }
    public void setStoreLatitude(double storeLatitude) { this.storeLatitude = storeLatitude; }
    public double getStoreLongitude() { return storeLongitude; }
    public void setStoreLongitude(double storeLongitude) { this.storeLongitude = storeLongitude; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Method to determine store type (for backward compatibility)
    public String getStoreType() {
        // You can modify this logic based on how you determine store type
        // For now, assume all stores are local unless specified otherwise
        return LOCAL_STORE;
    }
}
