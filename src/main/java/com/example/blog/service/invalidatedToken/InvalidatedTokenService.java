package com.example.blog.service.invalidatedToken;

import com.example.blog.model.InvalidatedToken;

public interface InvalidatedTokenService {
    boolean existsByIdWithRevoke(String id);
    InvalidatedToken save(InvalidatedToken invalidatedToken);
}
