package fpt.prm392.fe_salehunter.model.product;

import com.google.gson.annotations.SerializedName;

public class ProductRateModel {
    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    public ProductRateModel() {}

    public ProductRateModel(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
