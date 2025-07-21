package fpt.prm392.fe_salehunter.model.store;

import com.google.gson.annotations.SerializedName;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;

@Data
public class StorePageData {
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
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

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
    private String createdAt;

    @SerializedName("productsCount")
    private int productsCount = 0;

    @SerializedName("products")
    private ArrayList<ProductModel> products = new ArrayList<>();
}
