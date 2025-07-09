package com.example.blog.configuaration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.blog.dto.response.ApiResponse;
import com.example.blog.exception.CustomException;
import com.example.blog.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
       ObjectMapper objectMapper = new ObjectMapper();
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        String errMessage = "Invalid or expired token";

        // Kiểm tra xem có lỗi CustomException không
        Exception ex = (Exception) request.getAttribute("auth_error");
        if (ex instanceof CustomException customEx) {
            errorCode = customEx.getErrorCode();
            errMessage = customEx.getErrMessage();
        }

        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .errMessage(errMessage)
                        .build()));
        response.flushBuffer();
    }

}
