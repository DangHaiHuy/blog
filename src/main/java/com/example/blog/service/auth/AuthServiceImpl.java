package com.example.blog.service.auth;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.blog.dto.request.AuthRequest;
import com.example.blog.dto.request.RefreshTokenRequest;
import com.example.blog.dto.response.AuthResponse;
import com.example.blog.entity.User;
import com.example.blog.exception.CustomException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.model.InvalidatedToken;
import com.example.blog.service.invalidatedToken.InvalidatedTokenService;
import com.example.blog.service.user.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;
    @Value("${jwt.refresh-duration}")
    private long REFRESH_DURATION;
    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    private UserService userService;
    private InvalidatedTokenService invalidatedTokenService;

    @Autowired
    public AuthServiceImpl(UserService userService, InvalidatedTokenService invalidatedTokenService) {
        this.userService = userService;
        this.invalidatedTokenService = invalidatedTokenService;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        User user = userService.findByUsername(request.getUsername());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isValid) {
            throw new CustomException(ErrorCode.INVALID_KEY, "Incorrect password or username");
        }
        if (Boolean.FALSE.equals(user.getActivated())) {
            throw new CustomException(ErrorCode.NOT_ACTIVATED,
                    "You need to activate your account first, pleade check your email");
        }
        if (Boolean.TRUE.equals(user.getLocked())) {
            throw new CustomException(ErrorCode.USER_LOCKED, "Your account has been locked");
        }
        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);
        return AuthResponse.builder().authenticated(isValid).accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    private String generateToken(User user, boolean isRefreshToken) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("blog")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now()
                        .plus((isRefreshToken) ? REFRESH_DURATION : VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", (user.getRole() != null) ? user.getRole().toString() : "USER")
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("The code has an error");
        }
    }

    @Override
    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED, "Invalid JWT format");
        }

        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean valid = signedJWT.verify(jwsVerifier);

        if (invalidatedTokenService.existsByIdWithRevoke(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new CustomException(ErrorCode.UNAUTHENTICATED, "Token has expired");
        if (!(valid && expirationDate.after(new Date()))) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED, "Token has expired");
        }
        if (valid) {
            String username = signedJWT.getJWTClaimsSet().getSubject();
            User user = userService.findByUsername(username);
            if (Boolean.FALSE.equals(user.getActivated())) {
                throw new CustomException(ErrorCode.NOT_ACTIVATED,
                        "You need to activate your account first, pleade check your email");
            }
            if (Boolean.TRUE.equals(user.getLocked()))
                throw new CustomException(ErrorCode.USER_LOCKED, "Your account has been locked");
        }
        return signedJWT;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
            throws JOSEException, ParseException {
        SignedJWT signedJWT = verifyToken(refreshTokenRequest.getToken());

        String JwtId = signedJWT.getJWTClaimsSet().getJWTID();
        LocalDateTime expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().expiryTime(expiryTime).id(JwtId).build();
        invalidatedTokenService.save(invalidatedToken);
        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userService.findByUsername(username);
        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);
        return AuthResponse.builder().authenticated(true).accessToken(accessToken).refreshToken(refreshToken)
                .build();
    }
}
