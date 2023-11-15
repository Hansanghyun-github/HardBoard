package com.example.HardBoard.config.filter;

import com.auth0.jwt.exceptions.*;
import com.example.HardBoard.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class JwtExceptionHandlingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (IllegalArgumentException |
                 SignatureVerificationException |
                 TokenExpiredException |
                 JWTDecodeException |
                 MissingClaimException |
                 IncorrectClaimException ex){
            ObjectMapper mapper = new ObjectMapper();
            log.debug("Authentication failed");
            String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.FORBIDDEN, ex.getMessage(), null));

            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
