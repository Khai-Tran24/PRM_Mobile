package fpt.prm392.fe_salehunter.model.barcode;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response model for OpenFoodFacts API
 * API: https://world.openfoodfacts.org/api/v2/product/{barcode}.json
 * Documentation: https://openfoodfacts.github.io/openfoodfacts-server/api/
 */
public class OpenFoodFactsResponseModel {

    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private int status; // 1 = product found, 0 = product not found

    @SerializedName("status_verbose")
    private String statusVerbose;

    @SerializedName("product")
    private Product product;

    public static class Product {
        @SerializedName("_id")
        private String id;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("product_name_en")
        private String productNameEn;

        @SerializedName("brands")
        private String brands;

        @SerializedName("categories")
        private String categories;

        @SerializedName("quantity")
        private String quantity;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("image_front_url")
        private String imageFrontUrl;

        @SerializedName("image_front_small_url")
        private String imageFrontSmallUrl;

        @SerializedName("ingredients_text")
        private String ingredientsText;

        @SerializedName("ingredients_text_en")
        private String ingredientsTextEn;

        @SerializedName("allergens")
        private String allergens;

        @SerializedName("nutrition_grades")
        private String nutritionGrades; // Nutri-Score: a, b, c, d, e

        @SerializedName("nova_group")
        private int novaGroup; // NOVA group: 1-4

        @SerializedName("ecoscore_grade")
        private String ecoscoreGrade; // Eco-Score: a, b, c, d, e

        @SerializedName("countries")
        private String countries;

        @SerializedName("manufacturing_places")
        private String manufacturingPlaces;

        @SerializedName("origins")
        private String origins;

        @SerializedName("packaging")
        private String packaging;

        @SerializedName("stores")
        private String stores;

        @SerializedName("nutriscore_score")
        private Integer nutriscoreScore;

        @SerializedName("completeness")
        private Double completeness; // Data completeness percentage

        // Nutrition facts (per 100g)
        @SerializedName("nutriments")
        private Nutriments nutriments;

        public static class Nutriments {
            @SerializedName("energy-kcal_100g")
            private Double energyKcal100g;

            @SerializedName("fat_100g")
            private Double fat100g;

            @SerializedName("saturated-fat_100g")
            private Double saturatedFat100g;

            @SerializedName("carbohydrates_100g")
            private Double carbohydrates100g;

            @SerializedName("sugars_100g")
            private Double sugars100g;

            @SerializedName("fiber_100g")
            private Double fiber100g;

            @SerializedName("proteins_100g")
            private Double proteins100g;

            @SerializedName("salt_100g")
            private Double salt100g;

            @SerializedName("sodium_100g")
            private Double sodium100g;

            // Getters and setters
            public Double getEnergyKcal100g() { return energyKcal100g; }
            public void setEnergyKcal100g(Double energyKcal100g) { this.energyKcal100g = energyKcal100g; }

            public Double getFat100g() { return fat100g; }
            public void setFat100g(Double fat100g) { this.fat100g = fat100g; }

            public Double getSaturatedFat100g() { return saturatedFat100g; }
            public void setSaturatedFat100g(Double saturatedFat100g) { this.saturatedFat100g = saturatedFat100g; }

            public Double getCarbohydrates100g() { return carbohydrates100g; }
            public void setCarbohydrates100g(Double carbohydrates100g) { this.carbohydrates100g = carbohydrates100g; }

            public Double getSugars100g() { return sugars100g; }
            public void setSugars100g(Double sugars100g) { this.sugars100g = sugars100g; }

            public Double getFiber100g() { return fiber100g; }
            public void setFiber100g(Double fiber100g) { this.fiber100g = fiber100g; }

            public Double getProteins100g() { return proteins100g; }
            public void setProteins100g(Double proteins100g) { this.proteins100g = proteins100g; }

            public Double getSalt100g() { return salt100g; }
            public void setSalt100g(Double salt100g) { this.salt100g = salt100g; }

            public Double getSodium100g() { return sodium100g; }
            public void setSodium100g(Double sodium100g) { this.sodium100g = sodium100g; }
        }

        // Getters and setters for Product
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getProductNameEn() { return productNameEn; }
        public void setProductNameEn(String productNameEn) { this.productNameEn = productNameEn; }

        public String getBrands() { return brands; }
        public void setBrands(String brands) { this.brands = brands; }

        public String getCategories() { return categories; }
        public void setCategories(String categories) { this.categories = categories; }

        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getImageFrontUrl() { return imageFrontUrl; }
        public void setImageFrontUrl(String imageFrontUrl) { this.imageFrontUrl = imageFrontUrl; }

        public String getImageFrontSmallUrl() { return imageFrontSmallUrl; }
        public void setImageFrontSmallUrl(String imageFrontSmallUrl) { this.imageFrontSmallUrl = imageFrontSmallUrl; }

        public String getIngredientsText() { return ingredientsText; }
        public void setIngredientsText(String ingredientsText) { this.ingredientsText = ingredientsText; }

        public String getIngredientsTextEn() { return ingredientsTextEn; }
        public void setIngredientsTextEn(String ingredientsTextEn) { this.ingredientsTextEn = ingredientsTextEn; }

        public String getAllergens() { return allergens; }
        public void setAllergens(String allergens) { this.allergens = allergens; }

        public String getNutritionGrades() { return nutritionGrades; }
        public void setNutritionGrades(String nutritionGrades) { this.nutritionGrades = nutritionGrades; }

        public int getNovaGroup() { return novaGroup; }
        public void setNovaGroup(int novaGroup) { this.novaGroup = novaGroup; }

        public String getEcoscoreGrade() { return ecoscoreGrade; }
        public void setEcoscoreGrade(String ecoscoreGrade) { this.ecoscoreGrade = ecoscoreGrade; }

        public String getCountries() { return countries; }
        public void setCountries(String countries) { this.countries = countries; }

        public String getManufacturingPlaces() { return manufacturingPlaces; }
        public void setManufacturingPlaces(String manufacturingPlaces) { this.manufacturingPlaces = manufacturingPlaces; }

        public String getOrigins() { return origins; }
        public void setOrigins(String origins) { this.origins = origins; }

        public String getPackaging() { return packaging; }
        public void setPackaging(String packaging) { this.packaging = packaging; }

        public String getStores() { return stores; }
        public void setStores(String stores) { this.stores = stores; }

        public Integer getNutriscoreScore() { return nutriscoreScore; }
        public void setNutriscoreScore(Integer nutriscoreScore) { this.nutriscoreScore = nutriscoreScore; }

        public Double getCompleteness() { return completeness; }
        public void setCompleteness(Double completeness) { this.completeness = completeness; }

        public Nutriments getNutriments() { return nutriments; }
        public void setNutriments(Nutriments nutriments) { this.nutriments = nutriments; }

        /**
         * Get the best available product name
         * Prioritizes English name, falls back to general name
         */
        public String getBestProductName() {
            if (productNameEn != null && !productNameEn.trim().isEmpty()) {
                return productNameEn.trim();
            }
            if (productName != null && !productName.trim().isEmpty()) {
                return productName.trim();
            }
            return null;
        }

        /**
         * Get the best available ingredients text
         * Prioritizes English ingredients, falls back to general ingredients
         */
        public String getBestIngredientsText() {
            if (ingredientsTextEn != null && !ingredientsTextEn.trim().isEmpty()) {
                return ingredientsTextEn.trim();
            }
            if (ingredientsText != null && !ingredientsText.trim().isEmpty()) {
                return ingredientsText.trim();
            }
            return null;
        }

        /**
         * Get the best available product image URL
         * Prioritizes front image URLs with fallback hierarchy
         */
        public String getBestImageUrl() {
            if (imageFrontUrl != null && !imageFrontUrl.trim().isEmpty()) {
                return imageFrontUrl.trim();
            }
            if (imageFrontSmallUrl != null && !imageFrontSmallUrl.trim().isEmpty()) {
                return imageFrontSmallUrl.trim();
            }
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                return imageUrl.trim();
            }
            return null;
        }
    }

    // Main class getters and setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getStatusVerbose() { return statusVerbose; }
    public void setStatusVerbose(String statusVerbose) { this.statusVerbose = statusVerbose; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    /**
     * Check if the API call was successful and product was found
     */
    public boolean isSuccess() {
        return status == 1 && product != null;
    }

    /**
     * Get product name with null safety
     */
    public String getProductName() {
        if (isSuccess()) {
            return product.getBestProductName();
        }
        return null;
    }
}
