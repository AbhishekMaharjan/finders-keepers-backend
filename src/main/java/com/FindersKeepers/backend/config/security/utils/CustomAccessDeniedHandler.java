package com.FindersKeepers.backend.config.security.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component("customAccessDeniedHandler")

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HttpMessageConverter<String> messageConverter;

    private final ObjectMapper mapper;

    CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
        this.messageConverter = new StringHttpMessageConverter();
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        GlobalApiResponse response = new GlobalApiResponse();
        response.setMessage(e.getMessage());
        response.setData(null);
        response.setStatus(false);
        ServletServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(httpServletResponse);
        serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);
        messageConverter.write(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON, serverHttpResponse);
    }
}
