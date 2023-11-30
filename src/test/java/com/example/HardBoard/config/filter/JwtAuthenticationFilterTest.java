package com.example.HardBoard.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest { // TODO 성공 케이스 테스트 추가
    @Mock
    UserRepository userRepository;
    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;

    @Test
    @DisplayName("jwt 액세스 토큰을 이용해 인증한다")
    void authenticationUsingJwtAccessToken() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(Role.ROLE_USER)
                .build();

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        Mockito.doReturn(Optional.of(user))
                .when(userRepository)
                .findByEmail(ArgumentMatchers.anyString());

        Mockito.doReturn(JwtProperties.TOKEN_PREFIX + accessToken)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isNotNull();
        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(principal.getUser()).satisfies(u -> {
            assertThat(u.getEmail()).isEqualTo(email);
            assertThat(u.getNickname()).isEqualTo(nickname);
            assertThat(passwordEncoder.matches(password, u.getPassword())).isTrue();
                });
    }
    
    @Test
    @DisplayName("Authorization 헤더가 비어 있다면 그냥 통과한다")
    void emptyAuthorizationHeaderShouldPass() throws Exception {
        // when // then
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.atLeastOnce()).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더의 값이 Token Prefix로 시작하지 않는다면 실패한다")
    void notStartWithTokenPrefixBeFail() throws Exception {
        // given
        String invalidTokenPrefix = "invalid";
        Mockito.doReturn(invalidTokenPrefix)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        // when // then
        assertThatThrownBy(
                () -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                        .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("token prefix is wrong");

        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 Secret의 토큰을 보내면 실패한다")
    void wrongSecretTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512("wrong"));

        Mockito.doReturn(JwtProperties.TOKEN_PREFIX + accessToken)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        // when // then
        assertThatThrownBy(
                        () -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isInstanceOf(SignatureVerificationException.class)
                .hasMessage("The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA512");

        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    @DisplayName("만료된 토큰을 보내면 인증에 실패한다")
    void expiredTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() - JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        Mockito.doReturn(JwtProperties.TOKEN_PREFIX + accessToken)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        // when // then
        assertThatThrownBy(
                        () -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageStartingWith("The Token has expired");

        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 claim의 토큰을 보내면 실패한다")
    void wrongClaimTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("wrongEmail", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        Mockito.doReturn(JwtProperties.TOKEN_PREFIX + accessToken)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        // when // then
        assertThatThrownBy(
                        () -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Email claim is null");

        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 이메일의 토큰을 보내면 실패한다")
    void wrongEmailTokenBeFailAuthentication() throws Exception {
        // given
        String nickname = "husi";
        String email = "email@email";
        String password = "password";

        String accessToken = JWT.create()
                .withSubject(nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", email + "wrong")
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        Mockito.doReturn(JwtProperties.TOKEN_PREFIX + accessToken)
                .when(request)
                .getHeader(JwtProperties.HEADER_STRING);

        Mockito.doReturn(Optional.ofNullable(null))
                .when(userRepository)
                .findByEmail(ArgumentMatchers.anyString());

        // when // then
        assertThatThrownBy(
                        () -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Email is wrong");

        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }
}