package com.example.blog.service.auth;

import java.text.ParseException;

import com.example.blog.dto.request.AuthRequest;
import com.example.blog.dto.request.RefreshTokenRequest;
import com.example.blog.dto.response.AuthResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws JOSEException, ParseException;

    SignedJWT verifyToken(String token) throws JOSEException, ParseException;
}
