package com.example.blog.mapper;

import org.mapstruct.Mapper;

import com.example.blog.dto.request.RegisterRequest;
import com.example.blog.dto.response.UserDetailResponse;
import com.example.blog.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDetailResponse userToUserDetailResponse(User user);
    User registerRequestToUser(RegisterRequest registerRequest);
}
