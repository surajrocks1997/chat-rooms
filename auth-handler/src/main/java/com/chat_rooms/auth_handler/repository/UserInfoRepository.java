package com.chat_rooms.auth_handler.repository;

import com.chat_rooms.auth_handler.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findByEmail(String email);
}
