package com.example.blog.service.mail;

public interface EmailService {
    void sendMessage(String to, String subject, String text);
}
