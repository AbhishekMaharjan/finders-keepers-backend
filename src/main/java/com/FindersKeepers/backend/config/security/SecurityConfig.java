package com.FindersKeepers.backend.config.security;


import com.FindersKeepers.backend.config.security.error.CustomOAuth2AuthenticationEntryPoint;
import com.FindersKeepers.backend.config.security.handler.CustomAccessTokenResponseHandler;
import com.FindersKeepers.backend.config.security.handler.PasswordAuthenticationConverter;
import com.FindersKeepers.backend.config.security.handler.PasswordAuthenticationProvider;
import com.FindersKeepers.backend.config.security.handler.TokenGeneration;
import com.FindersKeepers.backend.config.security.jpa.JpaOAuth2AuthorizationService;
import com.FindersKeepers.backend.config.security.user.MyUserDetailsService;
import com.FindersKeepers.backend.config.security.utils.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.FindersKeepers.backend.constant.ConfigConstants.PUBLIC_MATCHERS;
import static com.FindersKeepers.backend.constant.ConfigConstants.SWAGGER_MATCHERS;


@Configuration
public class SecurityConfig {

    private final IntermediateHandler intermediateHandler;
    private final JpaOAuth2AuthorizationService jpaOAuth2AuthorizationService;
    private final MyUserDetailsService userDetailsService;
    private final CustomOAuth2AuthenticationEntryPoint customOAuth2AuthenticationEntryPoint;
    private final TokenGeneration tokenGeneration;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(IntermediateHandler intermediateHandler,
                          JpaOAuth2AuthorizationService jpaOAuth2AuthorizationService,
                          MyUserDetailsService userDetailsService,
                          CustomOAuth2AuthenticationEntryPoint customOAuth2AuthenticationEntryPoint,
                          TokenGeneration rsaConvertor,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAuthenticationProvider customAuthenticationProvider) {

        this.intermediateHandler = intermediateHandler;
        this.jpaOAuth2AuthorizationService = jpaOAuth2AuthorizationService;
        this.userDetailsService = userDetailsService;
        this.customOAuth2AuthenticationEntryPoint = customOAuth2AuthenticationEntryPoint;
        this.tokenGeneration = rsaConvertor;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    @Order(1) //authorization
    public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customOAuth2AuthenticationEntryPoint))
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(new PasswordAuthenticationConverter(intermediateHandler))
                        .authenticationProvider(passwordAuthenticationBean())
                        .accessTokenRequestConverters(getConverters())
                        .authenticationProviders(getProviders())
                        .accessTokenResponseHandler(new CustomAccessTokenResponseHandler())
                );
        return http.build();
    }

    @Bean
    public PasswordAuthenticationProvider passwordAuthenticationBean() {
        return new PasswordAuthenticationProvider(intermediateHandler, jpaOAuth2AuthorizationService, tokenGeneration.tokenGenerator(), userDetailsService, passwordEncoder());
    }


    private Consumer<List<AuthenticationProvider>> getProviders() {
        return a -> a.forEach(logs -> System.out.println(logs.toString()));
    }

    private Consumer<List<AuthenticationConverter>> getConverters() {

        return a -> a.forEach(logs -> System.out.println(logs.toString()));
    }

    @Bean
    @Order(2) //resource
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer.decoder(tokenGeneration.jwtDecoder(tokenGeneration.jwkSource())))
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authenticationProvider(customAuthenticationProvider)
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                        .successHandler(customAuthenticationSuccessHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_MATCHERS).permitAll().requestMatchers(SWAGGER_MATCHERS).hasAuthority("SWAGGER_USER").anyRequest().hasAuthority("OAUTH_USER"))
                .build();
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        config.setExposedHeaders(Arrays.asList("X-Auth-Token", "X-Custom-Header-1", "X-Custom-Header-2"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "OPTIONS", "POST", "PATCH", "PUT"));
        configuration.setExposedHeaders(Arrays.asList("X-Auth-Token", "X-Custom-Header-1", "X-Custom-Header-2"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }


}