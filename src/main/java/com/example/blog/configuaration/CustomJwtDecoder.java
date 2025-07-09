package com.example.blog.configuaration;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.blog.exception.CustomException;
import com.example.blog.service.auth.AuthService;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signer-key}")
    private String SIGNER_KEY;

    private AuthService authService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;
    private HttpServletRequest httpServletRequest;

    @Autowired
    public CustomJwtDecoder(AuthService authService,HttpServletRequest httpServletRequest) {
        this.authService = authService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            authService.verifyToken(token);
        } catch (CustomException e) {
            httpServletRequest.setAttribute("auth_error", e);
            throw new JwtException(e.getMessage(), e);
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
