package fpt.prm392.fe_salehunter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductsResponseModel extends BaseResponseModel {

    @SerializedName("results")
    private int resultsCount;

    @SerializedName("data")
    private ArrayList<ProductModel> products;

    public int getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(int resultsCount) {
        this.resultsCount = resultsCount;
    }

    public ArrayList<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductModel> products) {
        this.products = products;
    }

}
