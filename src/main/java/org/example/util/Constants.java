// src/main/java/org/example/util/Constants.java
package org.example.util;

public class Constants {

    // Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // JWT
    public static final long JWT_EXPIRATION = 86400000; // 24 часа
    public static final String JWT_SECRET = "your-secret-key-change-in-production";

    // Cache
    public static final String CACHE_USERS = "users";
    public static final String CACHE_TASKS = "tasks";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // API paths
    public static final String API_PREFIX = "/api";
    public static final String API_VERSION = "/v1";
    public static final String BASE_API_PATH = API_PREFIX + API_VERSION;
}