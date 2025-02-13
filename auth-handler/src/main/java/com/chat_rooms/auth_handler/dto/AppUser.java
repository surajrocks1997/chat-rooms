package com.chat_rooms.auth_handler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUser {

    @NotEmpty(message = "Name cannot be empty")
    @NotNull(message = "Name Cannot be Null")
    private String name;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password should be atleast 6 characters long")
    private String password;
}
