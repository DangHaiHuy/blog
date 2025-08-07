package com.example.blog.service.user;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.blog.constant.RedisConstantPrefix;
import com.example.blog.dto.request.RegisterRequest;
import com.example.blog.dto.request.ResetPasswordRequest;
import com.example.blog.dto.response.ActivateResponse;
import com.example.blog.dto.response.ListResponse;
import com.example.blog.dto.response.ResetPasswordResponse;
import com.example.blog.dto.response.SendOtpResponse;
import com.example.blog.dto.response.UserDetailResponse;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.exception.CustomException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.UserMapper;
import com.example.blog.model.TemplateEmail;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.mail.EmailService;
import com.example.blog.service.otp.OtpService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private TemplateEngine templateEngine;
    private OtpService otpService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy PasswordEncoder passwordEncoder,
            EmailService emailService, TemplateEngine templateEngine, OtpService otpService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.otpService = otpService;
    }

    @Override
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

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Account not found"));
    }

    @Override
    public UserDetailResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new CustomException(ErrorCode.EXISTED, "Username existed");
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new CustomException(ErrorCode.EXISTED, "Email existed");
        User user = userMapper.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        user.setLocked(false);
        user.setActivated(false);
        user.setActivateCode(String.format("%06d", new Random().nextInt(999999)));

        Context context = new Context();
        context.setVariable("code", user.getActivateCode());
        context.setVariable("title", "Activate your account");
        context.setVariable("message",
                "Thank you for signing up. Please use the verification code below to activate your account:");
        String htmlContent = templateEngine.process("email-template", context);
        emailService.sendMessage(user.getEmail(), "Web blog - Activate account", htmlContent);

        return userMapper.userToUserDetailResponse(userRepository.save(user));
    }

    @Override
    public ActivateResponse activateAccount(String code, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND, "Cannot find an account linked to this email"));
        if (Boolean.TRUE.equals(user.getActivated())) {
            return ActivateResponse.builder().result("Your account has been activated").build();
        }
        if (user.getActivateCode().equals(code)) {
            user.setActivated(true);
            userRepository.save(user);
            return ActivateResponse.builder().result("Your account has been successfully activated").build();
        }
        throw new CustomException(ErrorCode.INVALID_KEY, "Cannot activate");
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Cannot find account"));
        if (otpService.checkOtpCode(RedisConstantPrefix.RESET_PASSWORD_OTP.toString(), user.getId(),
                request.getOtpCode())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ResetPasswordResponse.builder().result("Change password successfully").build();
        } else {
            throw new CustomException(ErrorCode.INVALID_KEY, "The code you entered is invalid");
        }
    }

    @Override
    public SendOtpResponse getOtpResetPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Cannot find account"));
        otpService.sendOtpEmail(user, RedisConstantPrefix.RESET_PASSWORD_OTP.toString(), user.getId(),
                TemplateEmail.builder().title("Blog - Reset password").message(
                        "Please use the otp code below to activate your account (Code will expire in 5 minutes):")
                        .mainTitle("Web blog - Reset password")
                        .build());
        return SendOtpResponse.builder().result("Email sent successfully").build();
    }

}
