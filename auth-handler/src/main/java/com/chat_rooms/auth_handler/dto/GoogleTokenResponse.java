package com.chat_rooms.auth_handler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleTokenResponse {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
}
