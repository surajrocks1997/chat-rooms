package com.chat_rooms.auth_handler.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.chat_rooms.auth_handler.config.JwtConfigProperties;
import com.chat_rooms.auth_handler.dto.JWTResponse;
import com.chat_rooms.auth_handler.global.CustomException;
import com.chat_rooms.auth_handler.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtConfigProperties jwtConfigProperties;
    private final CookieUtil cookieUtil;
    private final RedisService redisService;

    private static final String REFRESH_TOKEN_VAR_NAME = "refreshToken";

    public JWTResponse getJwtResponse(HttpServletResponse response, String email, Long userId) {
        log.info("getJwtResponse flow started");
        String jwt = JWT.create()
                .withExpiresAt(Date.from(Instant.now().plus(1L, ChronoUnit.HOURS)))
                .withIssuer(jwtConfigProperties.getIss())
                .withAudience(jwtConfigProperties.getAud())
                .withSubject(email)
                .withClaim("id", userId)
                .sign(Algorithm.HMAC256(jwtConfigProperties.getSecret()));

        String refreshToken = UUID.randomUUID().toString();
        int maxAgeInSeconds = (8 * 60 * 60);
        storeRefreshToken(userId.toString(), refreshToken, maxAgeInSeconds);

        cookieUtil.create(response, REFRESH_TOKEN_VAR_NAME, refreshToken, false, maxAgeInSeconds, "localhost");

        log.info("getJwtResponse flow ended");
        return JWTResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(Duration.of(1L, ChronoUnit.HOURS).toSeconds())
                .build();
    }

    public void validateJWTToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConfigProperties.getSecret()))
                .withAudience(jwtConfigProperties.getAud())
                .withIssuer(jwtConfigProperties.getIss())
                .build();

        try {
            verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new CustomException("Token Expired", HttpStatus.UNAUTHORIZED);
        } catch (JWTVerificationException e) {
            throw new CustomException("Token is Not Valid", HttpStatus.UNAUTHORIZED);
        }
    }

    private void storeRefreshToken(String userId, String refreshToken, int ttl) {
        log.info("storeRefreshToken flow started");
        String key = "userId:" + userId;
        redisService.putWithExpiry(key, REFRESH_TOKEN_VAR_NAME, refreshToken, ttl);
        log.info("storeRefreshToken flow ended");
    }

    public boolean verifyRefreshTokenValidity(String userId) {
        log.info("verifyRefreshTokenValidity flow started");
        String key = "userId:" + userId;
        String refreshToken = redisService.get(key, REFRESH_TOKEN_VAR_NAME);
        if(refreshToken == null)
            throw new CustomException("Refresh Token Expired! Please LogIn Again", HttpStatus.BAD_REQUEST);

        log.info("verifyRefreshTokenValidity flow ended");
        return true;
    }
}
