package com.platzi.api.tests;

import com.platzi.api.models.Category;
import com.platzi.api.models.CategoryRequest;
import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CategoriesTests extends BaseTest {

    private static int createdCategoryId;

    // ==================== TC-CAT-001 ====================
    @Test(description = "GET /categories returns list with id, name, image, slug",
          groups = {"categories", "smoke"})
    public void TC_CAT_001_GetAllCategories() {
        log("Sending GET /categories");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.CATEGORIES_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$",         not(empty()))
                .body("[0].id",    notNullValue())
                .body("[0].name",  notNullValue())
                .body("[0].image", notNullValue())
                .body("[0].slug",  notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Categories list returned successfully");
    }

    // ==================== TC-CAT-002 ====================
    @Test(description = "GET /categories/{id} returns correct category",
          groups = {"categories"})
    public void TC_CAT_002_GetCategoryById_Valid() {
        log("Sending GET /categories/1");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.CATEGORIES_ENDPOINT + "/1")
        .then()
                .statusCode(200)
                .body("id",   equalTo(1))
                .body("name", notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        Category cat = response.as(Category.class);
        log("Category retrieved: " + cat.getName());
    }

    // ==================== TC-CAT-003 ====================
    @Test(description = "GET /categories/{id} non-existent ID returns 400/404",
          groups = {"categories", "negative"})
    public void TC_CAT_003_GetCategoryById_NotFound() {
        log("Sending GET /categories/99999");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.CATEGORIES_ENDPOINT + "/99999")
        .then()
                .extract().response();

        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 4xx, got: " + response.statusCode();

        log("Received " + response.statusCode() + " for non-existent category");
    }

    // ==================== TC-CAT-004 ====================
    @Test(description = "POST /categories creates a new category",
          groups = {"categories", "crud"})
    public void TC_CAT_004_CreateCategory_Valid() {
        log("Sending POST /categories");

        CategoryRequest request = CategoryRequest.builder()
                .name("QA Test Category")
                .image("https://placehold.co/600x400")
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.CATEGORIES_ENDPOINT)
        .then()
                .statusCode(201)
                .body("id",   notNullValue())
                .body("name", equalTo("QA Test Category"))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        Category created = response.as(Category.class);
        createdCategoryId = created.getId();
        log("Category created with ID: " + createdCategoryId);
    }

    // ==================== TC-CAT-005 ====================
    @Test(description = "POST /categories without name returns 400",
          groups = {"categories", "crud", "negative"})
    public void TC_CAT_005_CreateCategory_MissingName() {
        log("Sending POST /categories without name");

        CategoryRequest request = CategoryRequest.builder()
                .image("https://placehold.co/600x400")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.CATEGORIES_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for missing name as expected");
    }

    // ==================== TC-CAT-006 ====================
    @Test(description = "PUT /categories/{id} updates category name",
          dependsOnMethods = {"TC_CAT_004_CreateCategory_Valid"},
          groups = {"categories", "crud"})
    public void TC_CAT_006_UpdateCategory_Valid() {
        log("Sending PUT /categories/" + createdCategoryId);

        CategoryRequest request = CategoryRequest.builder()
                .name("Updated QA Category")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .put(ConfigManager.CATEGORIES_ENDPOINT + "/" + createdCategoryId)
        .then()
                .statusCode(200)
                .body("name", equalTo("Updated QA Category"))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Category updated successfully");
    }

    // ==================== TC-CAT-007 ====================
    @Test(description = "DELETE /categories/{id} deletes category",
          dependsOnMethods = {"TC_CAT_006_UpdateCategory_Valid"},
          groups = {"categories", "crud"})
    public void TC_CAT_007_DeleteCategory_Valid() {
        log("Sending DELETE /categories/" + createdCategoryId);

        given()
                .spec(requestSpec)
        .when()
                .delete(ConfigManager.CATEGORIES_ENDPOINT + "/" + createdCategoryId)
        .then()
                .statusCode(200)
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Category deleted successfully");
    }

    // ==================== TC-CAT-008 ====================
    @Test(description = "GET /categories/{id}/products returns products for that category",
          groups = {"categories"})
    public void TC_CAT_008_GetProductsByCategory() {
        log("Sending GET /categories/1/products");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.CATEGORIES_ENDPOINT + "/1/products")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Products by category returned successfully");
    }
}
