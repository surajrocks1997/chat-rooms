package com.chat_rooms.auth_handler.controller;

import com.chat_rooms.auth_handler.dto.GoogleTokenResponse;
import com.chat_rooms.auth_handler.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping("/token")
    public ResponseEntity<GoogleTokenResponse> generateToken(HttpServletRequest request) {
        String authCode = request.getHeader("authCode");
        GoogleTokenResponse res = googleAuthService.getTokenDetail(authCode);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
