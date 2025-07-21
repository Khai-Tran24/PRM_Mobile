package fpt.prm392.fe_salehunter.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
public class CreateProductRequestModel {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Double price;

    @SerializedName("salePercent")
    private Integer salePercent;

    @SerializedName("brand")
    private String brand;

    @SerializedName("category")
    private String category;

    @Builder.Default
    @SerializedName("images")
    private List<String> images = new ArrayList<>();
}

