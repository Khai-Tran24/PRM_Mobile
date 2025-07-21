package fpt.prm392.fe_salehunter.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentVerificationResponseModel {
    @SerializedName("transactionId")
    private String transactionId;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("completedAt")
    private String completedAt;
    
    @SerializedName("description")
    private String description;
    
    // Constructors
    public PaymentVerificationResponseModel() {}
    
    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Helper methods
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }
    
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status);
    }
}
