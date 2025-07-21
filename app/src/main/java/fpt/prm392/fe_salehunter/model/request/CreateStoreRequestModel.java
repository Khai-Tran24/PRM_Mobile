package fpt.prm392.fe_salehunter.model.request;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreRequestModel {
    @SerializedName("logoBase64")
    private String logoBase64;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("description")
    private String description;

    @SerializedName("phone")
    private String phone;

    @SerializedName("whatsappPhoneNumber")
    private String whatsappPhoneNumber;

    @SerializedName("websiteLink")
    private String websiteLink;

    @SerializedName("facebookLink")
    private String facebookLink;

    @SerializedName("instagramLink")
    private String instagramLink;
}
