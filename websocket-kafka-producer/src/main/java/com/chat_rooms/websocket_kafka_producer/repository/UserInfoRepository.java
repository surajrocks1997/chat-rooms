package com.chat_rooms.websocket_kafka_producer.repository;

import com.chat_rooms.websocket_kafka_producer.dto.UserInfoProjection;
import com.chat_rooms.websocket_kafka_producer.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfoProjection> findUserProjectionById(Long id);

    UserInfo findByEmail(String email);
}
