package com.FindersKeepers.backend.config.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        GlobalApiResponse response = new GlobalApiResponse();

        if (e.getMessage().contains("expired"))
            response.setMessage("Access Token has Expired");
        else if (e.getMessage().contains("Invalid signature")) {
            response.setMessage("Invalid Access Token");
        } else
            response.setMessage(e.getMessage());
        response.setData(null);
        response.setStatus(false);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(httpServletResponse.getOutputStream(), response);
    }
}

