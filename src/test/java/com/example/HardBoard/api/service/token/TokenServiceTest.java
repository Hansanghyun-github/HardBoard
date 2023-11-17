package com.example.HardBoard.api.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class TokenServiceTest {
    @Autowired TokenService tokenService;
    @Autowired UserRepository userRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("인증된 유저로 액세스토큰과 리프레시토큰을 발급한다")
    void createTokensUsingAuthenticatedUser() throws Exception {
        // given
        String email = "email@email";
        String nickname = "husi";
        String password = "password";
        User user = userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .password(password)
                        .build());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        PrincipalDetails principal = new PrincipalDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Long currentTimeMillis = System.currentTimeMillis();

        // when
        TokenResponse tokenResponse = tokenService.createTokens(currentTimeMillis);

        String accessToken = tokenResponse.getAccessToken()
                .replace(JwtProperties.TOKEN_PREFIX, "");
        String refreshToken = tokenResponse.getRefreshToken();

        String result = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException())
                .getRefreshToken();

        DecodedJWT verified = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build().verify(accessToken);

        // then
        assertThat(verified.getSubject()).isEqualTo(nickname);
        assertThat(verified.getClaim("email").asString()).isEqualTo(email);
        assertThat(verified.getExpiresAt()).isEqualTo(
                new Date( // JWT 라이브러리에서 액세스 토큰을 만들면, 해당 토큰의 expiredTime의 밀리초는 0으로 세팅 됨
                        ((currentTimeMillis+JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME)/1000)*1000
                ));

        assertThat(refreshToken).isEqualTo(result);
    }

    @Test
    @DisplayName("SecurityContext에 Authentication이 비어 있다면 토큰 생성에 실패한다")
    void createTokensInFailBecauseSecurityContextIsEmpty() throws Exception {
        // when // then
        assertThatThrownBy(() -> tokenService.createTokens(System.currentTimeMillis()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("액세스토큰이 만료되서 트큰들을 재생성한다")
    void accessTokenExpired() throws Exception {
        // given
        String email = "email@email";
        String nickname = "husi";
        String password = "password";

        User user = userRepository.save(
                User.builder()
                        .nickname(nickname)
                        .email(email)
                        .password(password)
                        .build());
        String refreshTokenName = "refreshToken";
        String refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshTokenName)
                        .user(user)
                        .expirationDate(LocalDateTime.now().plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                        .build()).getRefreshToken();

        Long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime localDateTime = Instant.ofEpochMilli(currentTimeMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // when
        TokenResponse tokenResponse = tokenService.accessTokenExpired(refreshTokenName, currentTimeMillis);

        DecodedJWT verified = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build().verify(tokenResponse.getAccessToken().replace(JwtProperties.TOKEN_PREFIX, ""));


        // then
        assertThat(verified.getSubject()).isEqualTo(nickname);
        assertThat(verified.getClaim("email").asString()).isEqualTo(email);
        assertThat(verified.getExpiresAt()).isEqualTo(
                new Date( // JWT 라이브러리에서 액세스 토큰을 만들면, 해당 토큰의 expiredTime의 밀리초는 0으로 세팅 됨
                        ((currentTimeMillis+JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME)/1000)*1000
                ));

        assertThat(tokenResponse.getRefreshToken()).isNotEqualTo(refreshTokenName);
    }

    @Test
    @DisplayName("리프레시토큰이 만료되었다면 재발급 실패한다")
    void ifRefreshTokenIsExpiredReCreateTokenInFail() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(
                User.builder()
                        .nickname(nickname)
                        .email(email)
                        .password(password)
                        .build());
        String refreshTokenName = "refreshToken";
        String refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .refreshToken(refreshTokenName)
                        .user(user)
                        .expirationDate(LocalDateTime.now().minusSeconds(2L))
                        .build()).getRefreshToken();

        // when // then
        assertThatThrownBy(() -> tokenService.accessTokenExpired(refreshTokenName, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RefreshToken was expired");
    }
    
    @Test
    @DisplayName("유효하지 않은 리프레시토큰이라면 재발급 실패한다")
    void ifRefreshTokenIsInvalidReCreateTokenInFail() throws Exception {
        // when // then
        String refreshToken = "refreshToken";
        assertThatThrownBy(() -> tokenService.accessTokenExpired(refreshToken, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token");
    }
}