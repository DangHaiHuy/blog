package com.example.blog.service.invalidatedToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.entity.InvalidatedToken;
import com.example.blog.repository.InvalidatedTokenRepository;

@Service
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService{
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    public InvalidatedTokenServiceImpl(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Override
    public boolean existsById(String id){
        return invalidatedTokenRepository.existsById(id);
    }

    @Override
    public InvalidatedToken save(InvalidatedToken invalidatedToken) {
        return invalidatedTokenRepository.save(invalidatedToken);
    }
}
