package com.chat_rooms.websocket_kafka_producer.repository;

import com.chat_rooms.websocket_kafka_producer.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findByEmail(String email);
}
