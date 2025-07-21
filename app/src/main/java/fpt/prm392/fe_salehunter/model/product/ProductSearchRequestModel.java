package fpt.prm392.fe_salehunter.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSearchRequestModel {
    private String query;
    private Long storeId;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy;
    private String brand;
}
