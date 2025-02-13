package com.chat_rooms.auth_handler.controller;

import com.chat_rooms.auth_handler.dto.AppUser;
import com.chat_rooms.auth_handler.dto.JWTResponse;
import com.chat_rooms.auth_handler.service.JWTService;
import com.chat_rooms.auth_handler.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AppAuthController {

    private final UserService userService;
    private final JWTService jwtService;

    //register user
    @PostMapping("/user")
    public ResponseEntity<JWTResponse> registerUser(@Valid @RequestBody AppUser appUser, HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("registerUser flow started");
        long userId = userService.validateAndRegister(appUser);

        JWTResponse jwtResponse = jwtService.getJwtResponse(response, appUser.getEmail(), userId);

        log.info("registerUser flow ended");
        return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
    }

    // login user
    @PostMapping
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody AppUser appUser, HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("login flow started");
        long userId = userService.validateLoginUser(appUser);

        JWTResponse jwtResponse = jwtService.getJwtResponse(response, appUser.getEmail(), userId);
        log.info("login flow ended");

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(HttpServletRequest request) {
        log.info("validateToken flow started");
        String authorization = request.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        jwtService.validateJWTToken(token);

        log.info("validateToken flow ended");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
