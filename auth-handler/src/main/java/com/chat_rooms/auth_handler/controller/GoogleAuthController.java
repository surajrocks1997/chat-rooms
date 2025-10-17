package com.chat_rooms.auth_handler.controller;

import com.chat_rooms.auth_handler.dto.GoogleTokenResponse;
import com.chat_rooms.auth_handler.dto.GoogleUserInfo;
import com.chat_rooms.auth_handler.dto.JWTResponse;
import com.chat_rooms.auth_handler.dto.MediaDownloadResult;
import com.chat_rooms.auth_handler.entity.UserInfo;
import com.chat_rooms.auth_handler.service.GoogleAuthService;
import com.chat_rooms.auth_handler.service.MediaService;
import com.chat_rooms.auth_handler.service.TokenService;
import com.chat_rooms.auth_handler.service.UserService;
import com.chat_rooms.auth_handler.utils.MediaOperationsUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final TokenService tokenService;
    private final MediaOperationsUtils mediaOperationsUtils;
    private final MediaService mediaService;

    @GetMapping("/token")
    public ResponseEntity<JWTResponse> generateTokenAndLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("generateToken flow started");
        String authCode = Objects.requireNonNull(request.getHeader("authCode"), "Auth Code cannot be null");
        log.info("AUTH_CODE: {}", authCode);
        GoogleTokenResponse res = googleAuthService.getTokenDetail(authCode);

        GoogleUserInfo userInfo = googleAuthService.getUserInfo(res.getAccess_token());

        Optional<UserInfo> user = userService.findUserByEmail(userInfo.getEmail());
        Long userId;

        if (user.isEmpty()) {
            userId = userService.saveGoogleUserToDb(userInfo);
            MediaDownloadResult mediaAndMetadata = mediaOperationsUtils.getMediaAndMetadata(userInfo.getProfilePictureUrl());
            ObjectId gridFsObjectId = mediaService.storeMedia(userId, mediaAndMetadata, "USER", "PROFILE_PICTURE");
            userService.updateUserProfilePicture(userId, gridFsObjectId);
        } else userId = user.get().getId();


        JWTResponse jwtResponse = tokenService.getJwtResponse(response, userId);

        log.info("generateToken flow ended");
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<JWTResponse> updateToken(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        // check if refresh token is valid or new SignIn required
        // if valid, generate new jwt and refresh token
        // update refresh token in cookie
        // send back new jwt token
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
