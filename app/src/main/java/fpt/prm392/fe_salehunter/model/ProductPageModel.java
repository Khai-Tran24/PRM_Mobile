package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductPageModel {
    private static final String WEBSITE_URL = "https://sale-hunter.vercel.app/";

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("currentPrice")
    private double currentPrice;

    @SerializedName("salePercent")
    private int salePercent;

    @SerializedName("finalPrice")
    private double finalPrice;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @SerializedName("images")
    private ArrayList<ProductImage> images;

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
    private Double storeLatitude;

    @SerializedName("storeLongitude")
    private Double storeLongitude;

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("updatedDate")
    private String updatedDate;

    @SerializedName("isActive")
    private boolean isActive;

    // Thêm trường userRating để hỗ trợ fragment
    private int userRating;

    public static class ProductImage {
        @SerializedName("id")
        private long id;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("isMainImage")
        private boolean isMainImage;

        @SerializedName("createdDate")
        private String createdDate;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl.replace("http:", "https:");
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isMainImage() {
            return isMainImage;
        }

        public void setMainImage(boolean mainImage) {
            this.isMainImage = mainImage;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(int salePercent) {
        this.salePercent = salePercent;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
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

    public ArrayList<ProductImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ProductImage> images) {
        this.images = images;
    }

    public String getMainImage() {
        return mainImage != null ? mainImage.replace("http:", "https:") : null;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImageUrl() {
        return storeImageUrl != null ? storeImageUrl.replace("http:", "https:") : null;
    }

    public void setStoreImageUrl(String storeImageUrl) {
        this.storeImageUrl = storeImageUrl;
    }

    public Double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(Double storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public Double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(Double storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getSourceUrl() {
        return mainImage != null ? mainImage.replace("http:", "https:") : null;
    }

    public String getShareableUrl() {
        return WEBSITE_URL + "pid=" + id;
    }
}