package fpt.prm392.fe_salehunter.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentInitRequestModel {
    @SerializedName("orderId")
    private String orderId;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("paymentMethod")
    private String paymentMethod; // MOMO, VNPAY, ZALOPAY
    
    @SerializedName("returnUrl")
    private String returnUrl;
    
    @SerializedName("notifyUrl")
    private String notifyUrl;
    
    @SerializedName("customerName")
    private String customerName;
    
    @SerializedName("customerPhone")
    private String customerPhone;
    
    @SerializedName("customerEmail")
    private String customerEmail;
    
    // Constructors
    public PaymentInitRequestModel() {}
    
    public PaymentInitRequestModel(String orderId, double amount, String description, 
                                 String paymentMethod, String returnUrl, String notifyUrl) {
        this.orderId = orderId;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
