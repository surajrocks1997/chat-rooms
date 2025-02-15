package com.chat_rooms.auth_handler.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void create(HttpServletResponse response, String name, String value, boolean secure, int maxAge, String domain) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(secure);
//        cookie.setMaxAge(maxAge);
//        cookie.setDomain(domain);
//        cookie.setPath("/");
//        response.addCookie(cookie);

        String cookieValue = String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax", name, value, maxAge);
        response.setHeader("Set-Cookie", cookieValue);
    }
}
