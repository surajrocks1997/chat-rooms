package com.chat_rooms.auth_handler.service;

import com.chat_rooms.auth_handler.dto.GoogleUserInfo;
import com.chat_rooms.auth_handler.entity.AuthProvider;
import com.chat_rooms.auth_handler.entity.UserInfo;
import com.chat_rooms.auth_handler.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public Long saveGoogleUserToDb(GoogleUserInfo googleUserInfo) {
        log.info("getTokenDetail flow started");
        UserInfo user = UserInfo.builder()
                .firstName(googleUserInfo.getGivenName())
                .lastName(googleUserInfo.getFamilyName())
                .email(googleUserInfo.getEmail())
                .authProvider(AuthProvider.GOOGLE.getValue())
                .isEmailVerified(googleUserInfo.isEmailVerified())
                .isSocialLogin(true)
                .profilePictureUrl(googleUserInfo.getProfilePictureUrl())
                .build();

        UserInfo savedUser = userInfoRepository.save(user);
        return savedUser.getId();
    }

}

