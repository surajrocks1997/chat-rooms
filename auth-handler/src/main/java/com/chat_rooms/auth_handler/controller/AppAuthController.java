package com.chat_rooms.auth_handler.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chat_rooms.auth_handler.dto.AppUser;
import com.chat_rooms.auth_handler.dto.JWTResponse;
import com.chat_rooms.auth_handler.service.TokenService;
import com.chat_rooms.auth_handler.service.UserService;
import com.chat_rooms.auth_handler.validation.ValidationGroup;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AppAuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private static final String AUTHORIZATION_HEADER_VAR_NAME = "Authorization";

    //register user
    @PostMapping("/user")
    public ResponseEntity<JWTResponse> registerUser(@Validated(ValidationGroup.Register.class) @RequestBody AppUser appUser,
                                                    HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("registerUser flow started");
        long userId = userService.validateAndRegister(appUser);

        JWTResponse jwtResponse = tokenService.getJwtResponse(response, appUser.getEmail(), userId);

        log.info("registerUser flow ended");
        return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
    }

    // login user
    @PostMapping
    public ResponseEntity<JWTResponse> login(@Validated(ValidationGroup.Login.class) @RequestBody AppUser appUser,
                                             HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("login flow started");
        long userId = userService.validateLoginUser(appUser);

        JWTResponse jwtResponse = tokenService.getJwtResponse(response, appUser.getEmail(), userId);
        log.info("login flow ended");

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    // validate token
    @GetMapping("/validateToken")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        log.info("validateToken flow started");
        String authorization = request.getHeader(AUTHORIZATION_HEADER_VAR_NAME);
        String token = authorization.substring(7);
        tokenService.validateJWTToken(token);

        log.info("validateToken flow ended");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // refreshToken
    @GetMapping("/refresh")
    public ResponseEntity<JWTResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("refreshToken flow started");

        JWTResponse jwtResponse = new JWTResponse();
        String authorization = request.getHeader(AUTHORIZATION_HEADER_VAR_NAME);
        DecodedJWT decode = JWT.decode(authorization.substring(7));
        Claim id = decode.getClaim("id");
        if (tokenService.verifyRefreshTokenValidity(String.valueOf(id))) {
            jwtResponse = tokenService.getJwtResponse(response, decode.getSubject(), id.asLong());
        }

        log.info("refreshToken flow ended");
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}
