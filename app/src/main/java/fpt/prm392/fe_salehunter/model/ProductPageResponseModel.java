package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

public class ProductPageResponseModel extends BaseResponseModel {

    @SerializedName("data")
    private ProductPageModel product;

    public ProductPageModel getProduct() {
        return product;
    }

    public void setProduct(ProductPageModel product) {
        this.product = product;
    }
}
