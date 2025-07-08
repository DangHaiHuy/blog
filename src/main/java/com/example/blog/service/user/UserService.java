package com.example.blog.service.user;

import com.example.blog.dto.response.ListResponse;
import com.example.blog.dto.response.UserDetailResponse;

public interface UserService {
    ListResponse<UserDetailResponse> getAllUsers(int page, int limit);
}
