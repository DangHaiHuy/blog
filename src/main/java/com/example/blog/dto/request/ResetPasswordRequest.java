package com.example.blog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String otpCode;
    private String username;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}
