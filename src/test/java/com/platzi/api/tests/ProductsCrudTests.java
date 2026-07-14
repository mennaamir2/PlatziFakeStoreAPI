package com.platzi.api.tests;
import com.platzi.api.models.Product;
import com.platzi.api.models.ProductRequest;
import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.Collections;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductsCrudTests extends BaseTest {

    private static int createdProductId;

    @Test
    public void TC_PROD_012_CreateProduct_Valid() {
        log("Sending POST /products with valid payload");

        ProductRequest request = ProductRequest.builder()
                .title("QA Test Product")
                .price(299)
                .description("Automated test product created by Rest Assured")
                .categoryId(1)
                .images(Collections.singletonList("https://placehold.co/600x400"))
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(201)
                .body("id",          notNullValue())
                .body("title",       equalTo("QA Test Product"))
                .body("price",       equalTo(299))
                .body("description", notNullValue())
                .body("category",    notNullValue())
                .body("images",      notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        Product created = response.as(Product.class);
        createdProductId = created.getId();
        log("Product created with ID: " + createdProductId);
    }

    @Test
    public void TC_PROD_013_CreateProduct_MissingTitle() {
        log("Sending POST /products without title");

        ProductRequest request = ProductRequest.builder()
                .price(100)
                .description("No title product")
                .categoryId(1)
                .images(Collections.singletonList("https://placehold.co/600x400"))
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for missing title as expected");
    }

    @Test
    public void TC_PROD_014_CreateProduct_NegativePrice() {
        log("Sending POST /products with price=-10");

        ProductRequest request = ProductRequest.builder()
                .title("Negative Price Product")
                .price(-10)
                .description("BVA test - negative price")
                .categoryId(1)
                .images(Collections.singletonList("https://placehold.co/600x400"))
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .extract().response();

        log("POST with negative price returned: " + response.statusCode());
        assert response.statusCode() == 201 || response.statusCode() == 400
                : "Unexpected status: " + response.statusCode();
    }

    @Test
    public void TC_PROD_015_CreateProduct_InvalidCategoryId() {
        log("Sending POST /products with non-existent categoryId=99999");

        ProductRequest request = ProductRequest.builder()
                .title("Invalid Category Product")
                .price(50)
                .description("Invalid category test")
                .categoryId(99999)
                .images(Collections.singletonList("https://placehold.co/600x400"))
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for invalid categoryId as expected");
    }

    @Test
    public void TC_PROD_016_CreateProduct_PriceZero() {
        log("Sending POST /products with price=0");

        ProductRequest request = ProductRequest.builder()
                .title("Zero Price Product")
                .price(0)
                .description("BVA test - price zero")
                .categoryId(1)
                .images(Collections.singletonList("https://placehold.co/600x400"))
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.PRODUCTS_ENDPOINT)
        .then()
                .extract().response();

        log("POST with price=0 returned: " + response.statusCode());
        assert response.statusCode() == 201 || response.statusCode() == 400
                : "Unexpected status: " + response.statusCode();
    }

    @Test(dependsOnMethods = {"TC_PROD_012_CreateProduct_Valid"})
    public void TC_PROD_017_UpdateProduct_Valid() {
        log("Sending PUT /products/" + createdProductId);

        ProductRequest request = ProductRequest.builder()
                .title("Updated QA Product")
                .price(499)
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .put(ConfigManager.PRODUCTS_ENDPOINT + "/" + createdProductId)
        .then()
                .statusCode(200)
                .body("title", equalTo("Updated QA Product"))
                .body("price", equalTo(499))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Product updated successfully");
    }

    @Test
    public void TC_PROD_018_UpdateProduct_NotFound() {
        log("Sending PUT /products/99999");

        ProductRequest request = ProductRequest.builder()
                .title("Ghost Update")
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .put(ConfigManager.PRODUCTS_ENDPOINT + "/99999")
        .then()
                .extract().response();

        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 4xx, got: " + response.statusCode();

        log("Received " + response.statusCode() + " for non-existent product");
    }

    @Test(dependsOnMethods = {"TC_PROD_017_UpdateProduct_Valid"})
    public void TC_PROD_019_DeleteProduct_Valid() {
        log("Sending DELETE /products/" + createdProductId);

        given()
                .spec(requestSpec)
        .when()
                .delete(ConfigManager.PRODUCTS_ENDPOINT + "/" + createdProductId)
        .then()
                .statusCode(200)
                .body(equalTo("true"))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Product deleted successfully");
    }

    @Test
    public void TC_PROD_020_DeleteProduct_NotFound() {
        log("Sending DELETE /products/99999");

        Response response = given()
                .spec(requestSpec)
        .when()
                .delete(ConfigManager.PRODUCTS_ENDPOINT + "/99999")
        .then()
                .extract().response();

        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 4xx, got: " + response.statusCode();

        log("Received " + response.statusCode() + " for non-existent delete");
    }
}
