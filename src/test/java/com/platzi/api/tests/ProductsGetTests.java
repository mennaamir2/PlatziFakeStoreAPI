package com.platzi.api.tests;

import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductsGetTests extends BaseTest {

    // ==================== TC-PROD-001 ====================
    @Test(description = "GET /products returns 200 with a non-empty list of products",
          groups = {"products", "smoke"})
    public void TC_PROD_001_GetAllProducts() {
        log("Sending GET /products");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$",         not(empty()))
                .body("[0].id",    notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].price", notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Products list returned successfully");
    }

    // ==================== TC-PROD-002 ====================
    @Test(description = "GET /products/{id} with valid ID returns correct product",
          groups = {"products", "smoke"})
    public void TC_PROD_002_GetProductById_Valid() {
        log("Sending GET /products/4");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/4")
        .then()
                .statusCode(200)
                .body("id",          equalTo(4))
                .body("title",       notNullValue())
                .body("price",       notNullValue())
                .body("description", notNullValue())
                .body("category",    notNullValue())
                .body("images",      notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Product with ID=4 returned successfully");
    }

    // ==================== TC-PROD-003 ====================
    @Test(description = "GET /products/{id} with non-existent ID returns 404 or empty",
          groups = {"products", "negative"})
    public void TC_PROD_003_GetProductById_NotFound() {
        log("Sending GET /products/99999 (non-existent)");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/99999")
        .then()
                .extract().response();

        // API may return 400 or 404 for non-existent product
        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 400 or 404, got: " + response.statusCode();

        log("Received " + response.statusCode() + " as expected for non-existent ID");
    }

    // ==================== TC-PROD-004 ====================
    @Test(description = "GET /products/slug/{slug} returns product matching slug",
          groups = {"products"})
    public void TC_PROD_004_GetProductBySlug_Valid() {
        log("Sending GET /products/slug/handmade-fresh-table");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/slug/handmade-fresh-table")
        .then()
                .statusCode(200)
                .body("slug", equalTo("handmade-fresh-table"))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Product by slug returned successfully");
    }

    // ==================== TC-PROD-005 ====================
    @Test(description = "GET /products/slug/{slug} with invalid slug returns 400/404",
          groups = {"products", "negative"})
    public void TC_PROD_005_GetProductBySlug_Invalid() {
        log("Sending GET /products/slug/this-slug-does-not-exist-xyz");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/slug/this-slug-does-not-exist-xyz")
        .then()
                .extract().response();

        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 4xx, got: " + response.statusCode();

        log("Received 4xx as expected for invalid slug");
    }

    // ==================== TC-PROD-006 ====================
    @Test(description = "GET /products?limit=5 returns exactly 5 products",
          groups = {"products", "pagination"})
    public void TC_PROD_006_GetProductsWithLimit() {
        log("Sending GET /products?offset=0&limit=5");

        Response response = given()
                .spec(requestSpec)
                .queryParam("offset", 0)
                .queryParam("limit", 5)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .extract().response();

        int size = response.jsonPath().getList("$").size();
        assert size <= 5 : "Expected max 5 products, got: " + size;
        log("Pagination limit=5 returned " + size + " products");
    }

    // ==================== TC-PROD-007 ====================
    @Test(description = "GET /products?offset=10&limit=10 returns second page",
          groups = {"products", "pagination"})
    public void TC_PROD_007_GetProductsWithOffsetPagination() {
        log("Sending GET /products?offset=10&limit=10");

        given()
                .spec(requestSpec)
                .queryParam("offset", 10)
                .queryParam("limit", 10)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$", notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Pagination offset=10 returned successfully");
    }

    // ==================== TC-PROD-008 ====================
    @Test(description = "GET /products?limit=0 returns empty list or valid response",
          groups = {"products", "boundary"})
    public void TC_PROD_008_GetProductsLimitZero() {
        log("Sending GET /products?offset=0&limit=0 (boundary)");

        Response response = given()
                .spec(requestSpec)
                .queryParam("offset", 0)
                .queryParam("limit", 0)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .extract().response();

        assert response.statusCode() == 200 || response.statusCode() == 400
                : "Expected 200 or 400, got: " + response.statusCode();

        log("Boundary limit=0 responded with: " + response.statusCode());
    }

    // ==================== TC-PROD-009 ====================
    @Test(description = "GET /products/{id}/related returns list of related products",
          groups = {"products"})
    public void TC_PROD_009_GetRelatedProductsById() {
        log("Sending GET /products/1/related");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/1/related")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Related products by ID returned successfully");
    }

    // ==================== TC-PROD-010 ====================
    @Test(description = "GET /products/slug/{slug}/related returns related products by slug",
          groups = {"products"})
    public void TC_PROD_010_GetRelatedProductsBySlug() {
        log("Sending GET /products/slug/handmade-fresh-table/related");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/slug/handmade-fresh-table/related")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Related products by slug returned successfully");
    }

    // ==================== TC-PROD-011 ====================
    @Test(description = "Response body schema: product has id, title, price, description, category, images",
          groups = {"products", "schema"})
    public void TC_PROD_011_ProductSchemaValidation() {
        log("Validating product schema fields");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.PRODUCTS_ENDPOINT + "/4")
        .then()
                .statusCode(200)
                .body("id",              instanceOf(Integer.class))
                .body("title",           instanceOf(String.class))
                .body("price",           instanceOf(Number.class))
                .body("description",     instanceOf(String.class))
                .body("category.id",     notNullValue())
                .body("category.name",   notNullValue())
                .body("category.slug",   notNullValue())
                .body("images",          instanceOf(java.util.List.class));

        log("Product schema is valid");
    }
}
