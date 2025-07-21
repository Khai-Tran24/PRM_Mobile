package fpt.prm392.fe_salehunter.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentInitResponseModel {
    @SerializedName("transactionId")
    private String transactionId;
    
    @SerializedName("paymentUrl")
    private String paymentUrl;
    
    @SerializedName("deepLink")
    private String deepLink;
    
    @SerializedName("qrCode")
    private String qrCode;
    
    @SerializedName("expiryTime")
    private String expiryTime;
    
    // Constructors
    public PaymentInitResponseModel() {}
    
    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
    
    public String getDeepLink() {
        return deepLink;
    }
    
    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
}
