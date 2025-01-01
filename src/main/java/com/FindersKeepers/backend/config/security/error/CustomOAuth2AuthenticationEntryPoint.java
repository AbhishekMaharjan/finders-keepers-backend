package com.FindersKeepers.backend.config.security.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_AUTHENTICATION;
import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_INVALID_CREDENTIAL;


@Component
public class CustomOAuth2AuthenticationEntryPoint extends BasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final CustomMessageSource customMessageSource;

    public CustomOAuth2AuthenticationEntryPoint(ObjectMapper objectMapper, CustomMessageSource customMessageSource) {
        this.objectMapper = objectMapper;
        this.customMessageSource = customMessageSource;
        setRealmName("Bad Credential");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(false);
        globalApiResponse.setData(null);
        if (authException instanceof BadCredentialsException)
            globalApiResponse.setMessage(customMessageSource.get(ERROR_INVALID_CREDENTIAL));
        else
            globalApiResponse.setMessage(customMessageSource.get(ERROR_AUTHENTICATION));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(globalApiResponse));
    }
}

