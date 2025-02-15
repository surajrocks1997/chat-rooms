package com.chat_rooms.auth_handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
