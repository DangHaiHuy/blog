package com.example.blog.service.user;

import com.example.blog.dto.request.RegisterRequest;
import com.example.blog.dto.request.ResetPasswordRequest;
import com.example.blog.dto.response.ActivateResponse;
import com.example.blog.dto.response.ListResponse;
import com.example.blog.dto.response.ResetPasswordResponse;
import com.example.blog.dto.response.SendOtpResponse;
import com.example.blog.dto.response.UserDetailResponse;
import com.example.blog.entity.User;

public interface UserService {
    ListResponse<UserDetailResponse> getAllUsers(int page, int limit);
    User findByUsername(String username);
    UserDetailResponse register(RegisterRequest registerRequest);
    ActivateResponse activateAccount(String code, String email);
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
    SendOtpResponse getOtpResetPassword(String username);
}
