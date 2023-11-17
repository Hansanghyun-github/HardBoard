package com.example.HardBoard.config.filter;

import com.auth0.jwt.exceptions.*;
import com.example.HardBoard.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class JwtExceptionHandlingFilter extends OncePerRequestFilter {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (IllegalArgumentException |
                 SignatureVerificationException |
                 JWTDecodeException |
                 MissingClaimException |
                 IncorrectClaimException ex){
            String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), null));

            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (TokenExpiredException ex){
            String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.BAD_REQUEST, "The Token has expired", null));

            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (AccessDeniedException ex){
            String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage(), null));

            response.getWriter().write(result);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
