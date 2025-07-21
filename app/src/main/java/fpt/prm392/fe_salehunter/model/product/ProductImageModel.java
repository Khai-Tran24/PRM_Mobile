package fpt.prm392.fe_salehunter.model.product;

import com.google.gson.annotations.SerializedName;

// Model to match backend ProductImageDto
public class ProductImageModel {
    @SerializedName("id")
    private long id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("isMainImage")
    private boolean isMainImage;

    @SerializedName("createdDate")
    private String createdDate;

    public ProductImageModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isMainImage() {
        return isMainImage;
    }

    public void setMainImage(boolean mainImage) {
        isMainImage = mainImage;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public ProductImageModel(long id, String imageUrl, boolean isMainImage, String createdDate) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isMainImage = isMainImage;
        this.createdDate = createdDate;
    }
}
