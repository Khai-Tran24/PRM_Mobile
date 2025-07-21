package fpt.prm392.fe_salehunter.model.product;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductModel {
    // Store type constants for fallback compatibility
    public static final String ONLINE_STORE = "online";
    public static final String LOCAL_STORE = "local";

    // Getters and setters
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("currentPrice")
    private double currentPrice; // Backend sends decimal but mobile expects double

    @SerializedName("salePercent")
    private Integer salePercent;

    @SerializedName("finalPrice") 
    private double finalPrice; // Calculated property from backend

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @SerializedName("images")
    private List<ProductImageModel> images; // Backend sends List<ProductImageDto>

    @SerializedName("mainImage")
    private String mainImage; // Calculated property from backend

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

    @SerializedName("createdDate")
    private String createdDate; // Backend sends DateTime but mobile expects String

    @SerializedName("updatedDate")
    private String updatedDate; // Backend sends DateTime but mobile expects String

    @SerializedName("storeLatitude")
    private double storeLatitude;

    @SerializedName("storeLongitude")
    private double storeLongitude;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("totalViews")
    private Integer totalViews;

    // Method to determine store type (for backward compatibility)
    public String getStoreType() {
        // You can modify this logic based on how you determine store type
        // For now, assume all stores are local unless specified otherwise
        return LOCAL_STORE;
    }
}
