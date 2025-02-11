package com.chat_rooms.auth_handler.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class PasswordUtils {

    public String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public byte[] decodeBase64(String encoded) {
        return Base64.getDecoder().decode(encoded);

    }
}