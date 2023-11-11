package com.example.HardBoard.config.filter;

import com.auth0.jwt.exceptions.*;
import com.example.HardBoard.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.FORBIDDEN, "Authentication failed"));

            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
