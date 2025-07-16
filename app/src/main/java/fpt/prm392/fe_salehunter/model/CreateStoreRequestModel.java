package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

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

    public String getLogoBase64() {
        return logoBase64;
    }

    public void setLogoBase64(String logoBase64) {
        this.logoBase64 = logoBase64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsappPhoneNumber() {
        return whatsappPhoneNumber;
    }

    public void setWhatsappPhoneNumber(String whatsappPhoneNumber) {
        this.whatsappPhoneNumber = whatsappPhoneNumber;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }
}
