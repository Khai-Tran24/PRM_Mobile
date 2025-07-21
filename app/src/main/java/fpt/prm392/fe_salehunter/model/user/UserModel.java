package fpt.prm392.fe_salehunter.model.user;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("profileImageUrl")
    private String image;

    @SerializedName("lastLoginDate")
    private String lastSeen; // Backend sends DateTime but mobile expects String

    @SerializedName("signedInWith")
    private int signedInWith;

    @SerializedName("storeId")
    private Long storeId;

    @SerializedName("accountType")
    private String accountType;

    @SerializedName("hasStore")
    private boolean hasStore;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("createdAt")
    private String createdAt; // Backend sends DateTime but mobile expects String

    public static final int SIGNED_IN_WITH_EMAIL = 0;
    public static final int SIGNED_IN_WITH_GOOGLE = 1;
    public static final int SIGNED_IN_WITH_FACEBOOK = 2;

    public UserModel(){
        id = 0;
        fullName = "fullName";
        email = "email@email.com";
        phoneNumber = "";
        image = "";
        lastSeen = "";
        signedInWith = SIGNED_IN_WITH_EMAIL;
        storeId = null;
        accountType = "";
        hasStore = false;
        isActive = true;
        createdAt = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageLink() {
        return image != null ? image.replace("http:","https:") : "";
    }

    public String getEncodedImage() {
        if(image != null && image.contains("http")) return "";
        return image != null ? image : "";
    }

    public void setEncodedImage(String image) {
        this.image = image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getSignedInWith() {
        return signedInWith;
    }

    public void setSignedInWith(int signedInWith) {
        this.signedInWith = signedInWith;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isHasStore() {
        return hasStore;
    }

    public void setHasStore(boolean hasStore) {
        this.hasStore = hasStore;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean hasStore(){
        return hasStore || (storeId != null && storeId > 0);
    }
}
