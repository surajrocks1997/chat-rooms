package com.chat_rooms.websocket_kafka_producer.service;

import com.chat_rooms.websocket_kafka_producer.dto.UserInfoProjection;
import com.chat_rooms.websocket_kafka_producer.entity.UserInfo;
import com.chat_rooms.websocket_kafka_producer.global.CustomException;
import com.chat_rooms.websocket_kafka_producer.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserInfoRepository userInfoRepository;

    public boolean doesUserExist(String email) {
        UserInfo user = findUserByEmail(email);
        return user != null;
    }

    public UserInfo findUserByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    public UserInfoProjection findUserByUsername(String username){
        Optional<UserInfoProjection> user = userInfoRepository.findUserByUsername(username);
        if (user.isEmpty()) throw new CustomException("No User Present with UserName : " + username, HttpStatus.BAD_REQUEST);
        return user.get();
    }

    public UserInfoProjection findUserById(Long id) {
        Optional<UserInfoProjection> user = userInfoRepository.findUserProjectionById(id);
        if (user.isEmpty()) throw new CustomException("No User Present with Given Id : " + id, HttpStatus.BAD_REQUEST);
        return user.get();
    }

}

