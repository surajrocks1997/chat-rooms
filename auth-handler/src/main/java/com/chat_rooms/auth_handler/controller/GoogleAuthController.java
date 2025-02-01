package com.chat_rooms.auth_handler.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.chat_rooms.auth_handler.config.JwtConfigProperties;
import com.chat_rooms.auth_handler.dto.GoogleTokenResponse;
import com.chat_rooms.auth_handler.dto.GoogleUserInfo;
import com.chat_rooms.auth_handler.dto.JWTResponse;
import com.chat_rooms.auth_handler.service.GoogleAuthService;
import com.chat_rooms.auth_handler.service.UserInfoService;
import com.chat_rooms.auth_handler.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final UserInfoService userInfoService;
    private final JwtConfigProperties jwtConfigProperties;
    private final CookieUtil cookieUtil;

    @GetMapping("/auth/google/token")
    public ResponseEntity<JWTResponse> generateToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("generateToken flow started");
        String authCode = request.getHeader("authCode");
        GoogleTokenResponse res = googleAuthService.getTokenDetail(authCode);

        // get profile information
        GoogleUserInfo userInfo = googleAuthService.getUserInfo(res.getAccess_token());

        // insert user in db
        Long userId = userInfoService.saveGoogleUserToDb(userInfo);

        // generate jwt
        String jwt = JWT.create()
                .withExpiresAt(Date.from(Instant.now().plus(1L, ChronoUnit.HOURS)))
                .withIssuer(jwtConfigProperties.getIss())
                .withAudience(jwtConfigProperties.getAud())
                .withSubject(userInfo.getEmail())
                .withClaim("id", userId)
                .sign(Algorithm.HMAC256(jwtConfigProperties.getSecret()));

        String refreshToken = UUID.randomUUID().toString();
        cookieUtil.create(response, "refreshToken", refreshToken, true, 8 * 60 * 60, "localhost");

        // send jwt response back
        JWTResponse jwtResponse = JWTResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(Duration.of(1L, ChronoUnit.HOURS).toSeconds())
                .build();

        log.info("generateToken flow ended");
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<JWTResponse> updateToken(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        // check if refresh token is valid or new SignIn required
        // if valid, generate new jwt and refresh token
        // update refresh token in cookie
        // send back new jwt token
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
