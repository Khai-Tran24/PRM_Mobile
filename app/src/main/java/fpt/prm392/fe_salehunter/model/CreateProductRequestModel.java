package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CreateProductRequestModel {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Double price;

    @SerializedName("salePercent")
    private Integer salePercent;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @SerializedName("images")
    private List<String> images;

    public CreateProductRequestModel() {
        this.images = new ArrayList<>();
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(Integer salePercent) {
        this.salePercent = salePercent;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // Added methods for backward compatibility
    public void setNameArabic(String nameArabic) {
        // For backward compatibility, set the name field
        this.name = nameArabic;
    }

    public void setSale(int salePercent) {
        this.salePercent = salePercent;
    }

    public void setCategoryArabic(String categoryArabic) {
        // For backward compatibility, set the category field
        this.category = categoryArabic;
    }

    public void setDescriptionArabic(String descriptionArabic) {
        // For backward compatibility, set the description field
        this.description = descriptionArabic;
    }
}

