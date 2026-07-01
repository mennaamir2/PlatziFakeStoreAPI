package com.platzi.api.tests;

import com.platzi.api.models.LoginRequest;
import com.platzi.api.models.LoginResponse;
import com.platzi.api.utils.BaseTest;
import com.platzi.api.utils.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseTest {


    @Test(description = "Login with valid credentials returns access_token and refresh_token",
          groups = {"auth", "smoke"})
    public void TC_AUTH_001_LoginValidCredentials() {
        log("Sending POST /auth/login with valid credentials");

        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.VALID_EMAIL)
                .password(ConfigManager.VALID_PASSWORD)
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(loginRequest)
        .when()
                .post(ConfigManager.AUTH_LOGIN_ENDPOINT)
        .then()
                .statusCode(201)
                .body("access_token",  notNullValue())
                .body("refresh_token", notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD))
                .extract().response();

        LoginResponse loginResponse = response.as(LoginResponse.class);
        BaseTest.accessToken = loginResponse.getAccessToken();
        log("Login successful. Token acquired: " + accessToken.substring(0, 20) + "...");
    }

    @Test(description = "Login with invalid password returns 401 Unauthorized",
          groups = {"auth", "negative"})
    public void TC_AUTH_002_LoginInvalidPassword() {
        log("Sending POST /auth/login with wrong password");

        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.VALID_EMAIL)
                .password(ConfigManager.INVALID_PASSWORD)
                .build();

        given()
                .spec(requestSpec)
                .body(loginRequest)
        .when()
                .post(ConfigManager.AUTH_LOGIN_ENDPOINT)
        .then()
                .statusCode(401);

        log("Received 401 as expected");
    }

    // ==================== TC-AUTH-003 ====================
    @Test(description = "Login with non-existent email returns 401",
          groups = {"auth", "negative"})
    public void TC_AUTH_003_LoginNonExistentEmail() {
        log("Sending POST /auth/login with non-existent email");

        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.INVALID_EMAIL)
                .password("anypassword")
                .build();

        given()
                .spec(requestSpec)
                .body(loginRequest)
        .when()
                .post(ConfigManager.AUTH_LOGIN_ENDPOINT)
        .then()
                .statusCode(401);

        log("Received 401 as expected");
    }

    // ==================== TC-AUTH-004 ====================
    @Test(description = "Login with missing email field returns 400",
          groups = {"auth", "negative"})
    public void TC_AUTH_004_LoginMissingEmail() {
        log("Sending POST /auth/login with missing email");

        LoginRequest loginRequest = LoginRequest.builder()
                .password(ConfigManager.VALID_PASSWORD)
                .build();

        given()
                .spec(requestSpec)
                .body(loginRequest)
        .when()
                .post(ConfigManager.AUTH_LOGIN_ENDPOINT)
        .then()
                .statusCode(anyOf(is(400), is(401)));

        log("Received 4xx as expected");
    }

    // ==================== TC-AUTH-005 ====================
    @Test(description = "Login with empty body returns 400",
          groups = {"auth", "negative"})
    public void TC_AUTH_005_LoginEmptyBody() {
        log("Sending POST /auth/login with empty body");

        LoginRequest loginRequest = new LoginRequest();

        given()
                .spec(requestSpec)
                .body(loginRequest)
        .when()
                .post(ConfigManager.AUTH_LOGIN_ENDPOINT)
        .then()
                .statusCode(anyOf(is(400), is(401)));

        log("Received 4xx as expected");
    }

    // ==================== TC-AUTH-006 ====================
    @Test(description = "GET /auth/profile with valid Bearer token returns user profile",
          dependsOnMethods = {"TC_AUTH_001_LoginValidCredentials"},
          groups = {"auth", "smoke"})
    public void TC_AUTH_006_GetProfileWithValidToken() {
        log("Sending GET /auth/profile with valid token");

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .get(ConfigManager.AUTH_PROFILE_ENDPOINT)
        .then()
                .statusCode(200)
                .body("id",    notNullValue())
                .body("email", notNullValue())
                .body("role",  notNullValue())
                .time(lessThan((long) ConfigManager.RESPONSE_TIME_THRESHOLD));

        log("Profile retrieved successfully");
    }

    // ==================== TC-AUTH-007 ====================
    @Test(description = "GET /auth/profile without token returns 401",
          groups = {"auth", "negative"})
    public void TC_AUTH_007_GetProfileWithoutToken() {
        log("Sending GET /auth/profile without Authorization header");

        given()
                .spec(requestSpec)
        .when()
                .get(ConfigManager.AUTH_PROFILE_ENDPOINT)
        .then()
                .statusCode(401);

        log("Received 401 as expected");
    }

    // ==================== TC-AUTH-008 ====================
    @Test(description = "GET /auth/profile with malformed Bearer token returns 401",
          groups = {"auth", "negative"})
    public void TC_AUTH_008_GetProfileWithInvalidToken() {
        log("Sending GET /auth/profile with fake token");

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer this.is.not.a.valid.token")
        .when()
                .get(ConfigManager.AUTH_PROFILE_ENDPOINT)
        .then()
                .statusCode(401);

        log("Received 401 as expected");
    }
}
