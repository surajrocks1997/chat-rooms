package com.chat_rooms.auth_handler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleUserInfo {

    private String id;
    private String email;

    @JsonProperty(value = "verified_email")
    private boolean isEmailVerified;

    @JsonProperty(value = "given_name")
    private String givenName;

    @JsonProperty(value = "family_name")
    private String familyName;

    @JsonProperty(value = "picture")
    private String profilePictureUrl;
}
