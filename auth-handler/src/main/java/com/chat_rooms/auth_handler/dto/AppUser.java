package com.chat_rooms.auth_handler.dto;

import com.chat_rooms.auth_handler.validation.ValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUser {

    @NotEmpty(message = "Name cannot be empty", groups = ValidationGroup.Register.class)
    @NotNull(message = "Name Cannot be Null", groups = ValidationGroup.Register.class)
    private String name;

    @Email(message = "Email should be valid", groups = {ValidationGroup.Login.class, ValidationGroup.Register.class})
    @NotNull(message = "Email cannot be null", groups = {ValidationGroup.Login.class, ValidationGroup.Register.class})
    private String email;

    @NotNull(message = "Password cannot be null", groups = {ValidationGroup.Login.class, ValidationGroup.Register.class})
    @Size(min = 6, message = "Password should be atleast 6 characters long", groups = {ValidationGroup.Login.class, ValidationGroup.Register.class})
    private String password;
}
