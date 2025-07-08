package com.example.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.blog.dto.response.ListResponse;
import com.example.blog.dto.response.UserDetailResponse;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ListResponse<UserDetailResponse> getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> pageUsers = userRepository.findAll(pageable);
        return ListResponse.<UserDetailResponse>builder()
                .items(pageUsers.getContent().stream().map((user) -> {
                    return userMapper.userToUserDetailResponse(user);
                }).toList())
                .total(pageUsers.getTotalElements())
                .build();
    }
}
