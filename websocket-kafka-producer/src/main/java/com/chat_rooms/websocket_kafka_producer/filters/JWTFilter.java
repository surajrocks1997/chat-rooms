package com.chat_rooms.websocket_kafka_producer.filters;

import com.auth0.jwt.JWT;
import com.chat_rooms.websocket_kafka_producer.dto.ErrorResponse;
import com.chat_rooms.websocket_kafka_producer.global.CustomException;
import com.chat_rooms.websocket_kafka_producer.service.AuthServerService;
import com.chat_rooms.websocket_kafka_producer.utils.LoggingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter implements Filter {

    private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";
    private final LoggingUtil loggingUtil;
    private final AuthServerService authServerService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("JWTFilter flow started");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            log.info("JWTFilter flow ending early because of HTTP METHOD Type OPTIONS");
            return;
        }

        String bearerToken = req.getHeader("Authorization");

        try {
            if (bearerToken == null || !bearerToken.startsWith("Bearer"))
                throw new CustomException("Invalid Token", HttpStatus.UNAUTHORIZED);

            authServerService.validateToken(bearerToken);
        } catch (CustomException e) {
            String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .errorMessage("JWTFilter Error : " + e.getMessage())
                    .correlationId(correlationId)
                    .timeStamp(LocalDateTime.now().toString())
                    .build();

            setCorsHeaders(res);
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

            loggingUtil.logException(e, correlationId);
            loggingUtil.logStructuredMessage(errorResponse);

            return;

        } catch (HttpStatusCodeException e) {
            String correlationId = MDC.get(CORRELATION_ID_LOG_VAR_NAME);
            ObjectMapper objectMapper = new ObjectMapper();
            ErrorResponse errorResponse = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);

            setCorsHeaders(res);
            res.setStatus(e.getStatusCode().value());
            res.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));

            loggingUtil.logException(e, correlationId);
            loggingUtil.logStructuredMessage(errorResponse);

            return;
        }


        Long claimId = JWT.decode(bearerToken.substring(7)).getClaim("id").asLong();
        req.setAttribute("claimId", claimId);

        chain.doFilter(req, res);
        log.info("JWTFilter flow ended");
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
    }
}
