package com.platzi.api.tests;

import com.platzi.api.models.User;
import com.platzi.api.models.UserRequest;
import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsersTests extends BaseTest {

    private static int    createdUserId;
    private static final String UNIQUE_EMAIL =
            "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@mail.com";

    // ==================== TC-USER-001 ====================
    @Test(description = "GET /users returns list with id, name, email, role",
          groups = {"users", "smoke"})
    public void TC_USER_001_GetAllUsers() {
        log("Sending GET /users");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$",           not(empty()))
                .body("[0].id",      notNullValue())
                .body("[0].name",    notNullValue())
                .body("[0].email",   notNullValue())
                .body("[0].role",    notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Users list returned successfully");
    }

    @Test(description = "GET /users/{id} returns correct user",
          groups = {"users"})
    public void TC_USER_002_GetUserById_Valid() {
        log("Sending GET /users/1");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.USERS_ENDPOINT + "/1")
        .then()
                .statusCode(200)
                .body("id",    equalTo(1))
                .body("email", notNullValue())
                .body("name",  notNullValue())
                .body("role",  notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        User user = response.as(User.class);
        log("User retrieved: " + user.getName() + " | " + user.getEmail());
    }

    // ==================== TC-USER-003 ====================
    @Test(description = "GET /users/{id} non-existent ID returns 400/404",
          groups = {"users", "negative"})
    public void TC_USER_003_GetUserById_NotFound() {
        log("Sending GET /users/99999");

        Response response = given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.USERS_ENDPOINT + "/99999")
        .then()
                .extract().response();

        assert response.statusCode() == 400 || response.statusCode() == 404
                : "Expected 4xx, got: " + response.statusCode();

        log("Received " + response.statusCode() + " for non-existent user");
    }

    // ==================== TC-USER-004 ====================
    @Test(description = "POST /users creates a new user with valid payload",
          groups = {"users", "crud"})
    public void TC_USER_004_CreateUser_Valid() {
        log("Sending POST /users with email: " + UNIQUE_EMAIL);

        UserRequest request = UserRequest.builder()
                .name("QA Test User")
                .email(UNIQUE_EMAIL)
                .password("Test1234")
                .avatar("https://placehold.co/60x60")
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(201)
                .body("id",    notNullValue())
                .body("name",  equalTo("QA Test User"))
                .body("email", equalTo(UNIQUE_EMAIL))
                .body("role",  notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        User created = response.as(User.class);
        createdUserId = created.getId();
        log("User created with ID: " + createdUserId);
    }

    // ==================== TC-USER-005 ====================
    @Test(description = "POST /users with duplicate email returns 400",
          groups = {"users", "crud", "negative"})
    public void TC_USER_005_CreateUser_DuplicateEmail() {
        log("Sending POST /users with existing email");

        UserRequest request = UserRequest.builder()
                .name("Duplicate User")
                .email(ConfigManager.VALID_EMAIL)
                .password("Test1234")
                .avatar("https://placehold.co/60x60")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for duplicate email as expected");
    }

    // ==================== TC-USER-006 ====================
    @Test(description = "POST /users without email field returns 400",
          groups = {"users", "crud", "negative"})
    public void TC_USER_006_CreateUser_MissingEmail() {
        log("Sending POST /users without email");

        UserRequest request = UserRequest.builder()
                .name("No Email User")
                .password("Test1234")
                .avatar("https://placehold.co/60x60")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for missing email as expected");
    }

    // ==================== TC-USER-007 ====================
    @Test(description = "POST /users with invalid email format returns 400",
          groups = {"users", "crud", "negative"})
    public void TC_USER_007_CreateUser_InvalidEmailFormat() {
        log("Sending POST /users with malformed email");

        UserRequest request = UserRequest.builder()
                .name("Bad Email User")
                .email("not-an-email")
                .password("Test1234")
                .avatar("https://placehold.co/60x60")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(400);

        log("Received 400 for invalid email format as expected");
    }

    // ==================== TC-USER-008 ====================
    @Test(description = "PUT /users/{id} updates name of created user",
          dependsOnMethods = {"TC_USER_004_CreateUser_Valid"},
          groups = {"users", "crud"})
    public void TC_USER_008_UpdateUser_Valid() {
        log("Sending PUT /users/" + createdUserId);

        UserRequest request = UserRequest.builder()
                .name("Updated QA User")
                .build();

        given()
                .spec(requestSpec)
                .body(request)
        .when()
                .put(ConfigManager.USERS_ENDPOINT + "/" + createdUserId)
        .then()
                .statusCode(200)
                .body("name", equalTo("Updated QA User"))
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("User updated successfully");
    }

    // ==================== TC-USER-009 ====================
    @Test(description = "DELETE /users/{id} deletes the created user",
          dependsOnMethods = {"TC_USER_008_UpdateUser_Valid"},
          groups = {"users", "crud"})
    public void TC_USER_009_DeleteUser_Valid() {
        log("Sending DELETE /users/" + createdUserId);

        given()
                .spec(requestSpec)
        .when()
                .delete(ConfigManager.USERS_ENDPOINT + "/" + createdUserId)
        .then()
                .statusCode(200)
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("User deleted successfully");
    }

    // ==================== TC-USER-010 ====================
    @Test(description = "GET /users with limit=5 returns max 5 users",
          groups = {"users", "pagination"})
    public void TC_USER_010_GetUsersWithPagination() {
        log("Sending GET /users?offset=0&limit=5");

        Response response = given()
                .spec(requestSpec)
                .queryParam("offset", 0)
                .queryParam("limit",  5)
        .when()
                .get(ConfigManager.USERS_ENDPOINT)
        .then()
                .statusCode(200)
                .extract().response();

        int size = response.jsonPath().getList("$").size();
        assert size <= 5 : "Expected max 5 users, got: " + size;
        log("Users pagination limit=5 returned " + size + " users");
    }
}
