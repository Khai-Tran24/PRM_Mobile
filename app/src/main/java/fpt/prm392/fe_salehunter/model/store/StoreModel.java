package fpt.prm392.fe_salehunter.model.store;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StoreModel {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("logoUrl")
    private String logoUrl;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("latitude")
    private Double latitude; // Backend sends decimal but mobile expects Double

    @SerializedName("longitude")
    private Double longitude; // Backend sends decimal but mobile expects Double

    @SerializedName("whatsappPhone")
    private String whatsappPhone;

    @SerializedName("facebookUrl")
    private String facebookUrl;

    @SerializedName("instagramUrl")
    private String instagramUrl;

    @SerializedName("websiteUrl")
    private String websiteUrl;

    @SerializedName("userId")
    private long userId;

    @SerializedName("createdAt")
    private String createdAt; // Backend sends DateTime but mobile expects String

    public StoreModel() {
    }

    public StoreModel(long id, String name, String type, String logoUrl, String phone, String address, String category, String description, Double latitude, Double longitude, String whatsappPhone, String facebookUrl, String instagramUrl, String websiteUrl, long userId, String createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.logoUrl = logoUrl;
        this.phone = phone;
        this.address = address;
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.whatsappPhone = whatsappPhone;
        this.facebookUrl = facebookUrl;
        this.instagramUrl = instagramUrl;
        this.websiteUrl = websiteUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }

}

