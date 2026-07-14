package com.platzi.api.tests;
import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductsFilterTests extends BaseTest {

    @Test
    public void TC_FILTER_001_FilterByTitle() {
        log("Sending GET /products?title=Generic");

        given()
                .spec(requestSpec)
                .queryParam("title", "Generic")
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Filter by title executed successfully");
    }

    @Test
    public void TC_FILTER_002_FilterByPriceRange() {
        log("Sending GET /products?price_min=10&price_max=100");

        Response response = given()
                .spec(requestSpec)
                .queryParam("price_min", 10)
                .queryParam("price_max", 100)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .extract().response();

        List<Integer> prices = response.jsonPath().getList("price");
        if (prices != null && !prices.isEmpty()) {
            for (int price : prices) {
                assert price >= 10 && price <= 100
                        : "Product price " + price + " is outside [10, 100] range";
            }
        }
        log("All returned products are within price range [10, 100]");
    }

    @Test
    public void TC_FILTER_003_FilterByCategoryId() {
        log("Sending GET /products?categoryId=1");

        Response response = given()
                .spec(requestSpec)
                .queryParam("categoryId", 1)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .extract().response();

        List<Integer> categoryIds = response.jsonPath().getList("category.id");
        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (int catId : categoryIds) {
                assert catId == 1
                        : "Returned product has categoryId=" + catId + " but expected 1";
            }
        }
        log("All returned products belong to categoryId=1");
    }

    @Test
    public void TC_FILTER_004_FilterByTitle_NoMatch() {
        log("Sending GET /products?title=XXXNOTEXIST");

        Response response = given()
                .spec(requestSpec)
                .queryParam("title", "XXXNOTEXIST_9999")
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .extract().response();

        List<?> products = response.jsonPath().getList("$");
        assert products == null || products.isEmpty()
                : "Expected empty list for non-existent title, got: " + products.size();

        log("Empty list returned for non-matching title filter");
    }

    @Test
    public void TC_FILTER_005_FilterInvertedPriceRange() {
        log("Sending GET /products?price_min=500&price_max=100 (inverted range)");

        Response response = given()
                .spec(requestSpec)
                .queryParam("price_min", 500)
                .queryParam("price_max", 100)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .extract().response();

        assert response.statusCode() == 200 || response.statusCode() == 400
                : "Unexpected status for inverted range: " + response.statusCode();

        log("Inverted range responded with: " + response.statusCode());
    }
}
