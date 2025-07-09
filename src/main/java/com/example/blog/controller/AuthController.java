package com.example.blog.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.request.AuthRequest;
import com.example.blog.dto.request.RefreshTokenRequest;
import com.example.blog.dto.response.ApiResponse;
import com.example.blog.dto.response.AuthResponse;
import com.example.blog.service.auth.AuthService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> authenticate(@RequestBody @Valid AuthRequest request) {
        return ApiResponse.<AuthResponse>builder().result(authService.authenticate(request)).build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws JOSEException, ParseException {
        return ApiResponse.<AuthResponse>builder().result(authService.refreshToken(request))
                .build();
    }
}
