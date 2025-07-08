package com.example.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
