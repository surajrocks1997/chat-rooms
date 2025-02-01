package com.chat_rooms.auth_handler.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JWTResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
