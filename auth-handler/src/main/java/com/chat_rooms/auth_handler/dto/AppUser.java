package com.chat_rooms.auth_handler.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class AppUser {

    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 6)
    private String password;
}
