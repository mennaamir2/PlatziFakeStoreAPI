package com.platzi.api.utils;

public class ConfigManager {

    public static final String BASE_URL = "https://api.escuelajs.co/api/v1";

    // Default test credentials (existing user in Platzi Fake Store)
    public static final String VALID_EMAIL    = "john@mail.com";
    public static final String VALID_PASSWORD = "changeme";

    // Invalid credentials for negative tests
    public static final String INVALID_EMAIL    = "notexist@mail.com";
    public static final String INVALID_PASSWORD = "wrongpassword";

    // Endpoints
    public static final String PRODUCTS_ENDPOINT      = "/products";
    public static final String CATEGORIES_ENDPOINT    = "/categories";
    public static final String USERS_ENDPOINT         = "/users";
    public static final String AUTH_LOGIN_ENDPOINT    = "/auth/login";
    public static final String AUTH_PROFILE_ENDPOINT  = "/auth/profile";
    public static final String AUTH_REFRESH_ENDPOINT  = "/auth/refresh-token";

    // Timeout (ms)
    public static final int RESPONSE_TIME_THRESHOLD = 5000;
}
