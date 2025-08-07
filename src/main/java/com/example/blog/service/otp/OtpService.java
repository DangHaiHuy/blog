package com.example.blog.service.otp;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.blog.entity.User;
import com.example.blog.exception.CustomException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.model.TemplateEmail;
import com.example.blog.service.mail.EmailService;

@Service
public class OtpService {
    private EmailService emailService;
    private RedisTemplate redisTemplate;
    private TemplateEngine templateEngine;

    @Autowired
    public OtpService(EmailService emailService, RedisTemplate redisTemplate, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.templateEngine = templateEngine;
    }

    public void sendOtpEmail(User receiver, String prefix, String id, TemplateEmail templateEmail) {
        if (Boolean.FALSE.equals(receiver.getActivated())) {
            throw new CustomException(ErrorCode.NOT_ACTIVATED,
                    "You need to activate your account first, pleade check your email");
        }
        if (receiver.getEmail() != null) {
            String otpCode = generateOtp(prefix, id);
            Context context = new Context();
            context.setVariable("code", otpCode);
            context.setVariable("title", templateEmail.getTitle());
            context.setVariable("message",
                    templateEmail.getMessage());
            String htmlContent = templateEngine.process("email-template", context);
            emailService.sendMessage(receiver.getEmail(), templateEmail.getMainTitle(), htmlContent);
        } else
            throw new CustomException(ErrorCode.NOT_FOUND, "Cannot find your email");
    }

    private String generateOtp(String prefix, String id) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(prefix + "::" + id, otpCode,
                Duration.ofMinutes(5));
        return otpCode;
    }

    public boolean checkOtpCode(String prefix, String id, String otp) {
        String key = prefix + "::" + id;
        String storedOtp = (String) redisTemplate.opsForValue().get(key);
        if (storedOtp == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "The code either doesn't exist or has expired");
        }
        if (!storedOtp.equals(otp)) {
            return false;
        }
        redisTemplate.delete(key);
        return true;
    }
}
