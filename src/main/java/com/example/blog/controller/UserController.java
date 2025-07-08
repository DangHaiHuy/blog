package com.example.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.response.ApiResponse;
import com.example.blog.dto.response.ListResponse;
import com.example.blog.dto.response.UserDetailResponse;
import com.example.blog.service.user.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-all-users")
    public ApiResponse<ListResponse<UserDetailResponse>> getAllUsers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<ListResponse<UserDetailResponse>>builder().result(userService.getAllUsers(page - 1, limit))
                .build();
    }
    
}
