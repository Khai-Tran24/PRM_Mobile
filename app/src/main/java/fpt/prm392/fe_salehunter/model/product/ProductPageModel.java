package fpt.prm392.fe_salehunter.model.product;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// MAJOR REFACTOR: Backend sends simple ProductDto, not complex nested structure
// This model now matches ProductDto and provides fallbacks for expected nested data
@Getter
@Builder
public class ProductPageModel {
    // Main ProductDto fields that backend actually sends
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

    @Builder.Default
    @SerializedName("images")
    private List<ProductImageDto> images = new ArrayList<>();

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

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("updatedDate")
    private String updatedDate;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("storeLatitude")
    private double storeLatitude;

    @SerializedName("storeLongitude")
    private double storeLongitude;

    @Builder.Default
    @SerializedName("totalViews")
    private Integer totalViews = 0;

    @SerializedName("prices")
    private List<ProductPriceHistoryDto> prices;

    public int getUserRating() {
        return 0; // Default: user hasn't rated yet
    }

    public void setUserRating(int userRating) {
        // Note: Backend doesn't support user ratings yet, this is for compatibility
    }

    @Data
    public static class ProductImageDto {
        @SerializedName("id")
        private long id;
        @SerializedName("imageUrl")
        private String imageUrl;
        @SerializedName("isMainImage")
        private boolean isMainImage;
        @SerializedName("createdDate")
        private String createdDate;

    }

    @Data
    public static class ProductPriceHistoryDto {
        @SerializedName("id")
        private Long id;
        @SerializedName("price")
        private Float price;
        @SerializedName("discountedPrice")
        private double discountedPrice;
        @SerializedName("isCurrentPrice")
        private Boolean isCurrentPrice;
        @SerializedName("createdDate")
        private String createdDate;
    }
}