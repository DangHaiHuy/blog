package com.example.blog.service.invalidatedToken;

import com.example.blog.entity.InvalidatedToken;

public interface InvalidatedTokenService {
    boolean existsById(String id);
    InvalidatedToken save(InvalidatedToken invalidatedToken);
}
