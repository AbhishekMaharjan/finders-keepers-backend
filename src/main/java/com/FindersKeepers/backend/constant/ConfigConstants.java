package com.FindersKeepers.backend.constant;

public class ConfigConstants {

    public static String[] PUBLIC_MATCHERS = {
            "/",
            "oauth2/refresh_token",
            "/country",
            "/users/save",
            "/item-category",
            "/item-category/**",
            };

    public static String[] SWAGGER_MATCHERS = {"/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/configuration/ui",
            "/configuration/security",
            "/oauth2/swagger_login"};
}
