package com.example.blog.service.invalidatedToken;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.blog.model.InvalidatedToken;

@Service
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService {
    private RedisTemplate redisTemplate;

    @Autowired
    public InvalidatedTokenServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean existsByIdWithRevoke(String id) {
        String status = (String) redisTemplate.opsForValue().get("token::" + id);
        return "revoked".equals(status);
    }

    @Override
    public InvalidatedToken save(InvalidatedToken invalidatedToken) {
        redisTemplate.opsForValue().set("token::" + invalidatedToken.getId(), "revoked",
                Duration.between(LocalDateTime.now(), invalidatedToken.getExpiryTime()));
        return invalidatedToken;
    }
}
