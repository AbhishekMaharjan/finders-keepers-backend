package com.FindersKeepers.backend.controller;



import com.FindersKeepers.backend.config.security.handler.RefreshTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "oauth2")
public class AuthorizationController {

    private final RefreshTokenHandler refreshTokenHandler;

    @GetMapping(value = "refresh_token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader(name = "Authorization") String authorization,
                                                @RequestParam(name = "refresh_token") String refreshToken,
                                                HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .ok()
                .body(refreshTokenHandler.generateAccessToken(refreshToken, httpServletRequest, authorization));
    }
}