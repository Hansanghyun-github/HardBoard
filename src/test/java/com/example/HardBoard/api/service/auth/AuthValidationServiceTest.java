package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class AuthValidationServiceTest {
    @Autowired AuthValidationService authValidationService;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired UserRepository userRepository;
    @Autowired AuthService authService;
    UserConverter userConverter = new UserConverter(new BCryptPasswordEncoder());
    @Test
    @DisplayName("SecurityContext의 userId와 path의 userId가 같다")
    @WithMockUser
    void contextUserIdAndPathUserIdIsSame() throws Exception {
        // given
        String email="fdsaf@fds";
        String password = "sadf";
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build()));

        authService.login(new AuthLoginServiceRequest(email, password));

        Long userId = user.getId();
        
        // when // then
        authValidationService.verifyPathUserId(userId);
    }

    @Test
    @DisplayName("SecurityContext의 userId와 path의 userId가 다르면 실패한다")
    void FailContextUserIdAndPathUserIdIsDifferent() throws Exception {
        // given
        String email="fdsaf@fds";
        String password = "sadf";
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build()));

        authService.login(new AuthLoginServiceRequest(email, password));

        Long userId = user.getId() + 1L;

        // when // then
        assertThatThrownBy(() -> authValidationService.verifyPathUserId(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid userId");
    }

    @Test
    @DisplayName("유저id와 리프레시토큰을 이용해서 RefreshToken 객체를 검증한다")
    void verifyRefreshToken() throws Exception {
        // given;
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build());
        Long userId = user.getId();

        String token = refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDateTime(LocalDateTime.now().plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME - 1L))
                .build()).getRefreshToken();

        // when // then
        authValidationService.verifyRefreshToken(token);
    }

    @Test
    @DisplayName("리프레시토큰에 해당하는 객체가 없기 때문에, verify에 실패한다")
    void failVerifyRefreshTokenFromInvalidRefreshToken() throws Exception {
        // given;
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build());
        Long userId = user.getId();

        String token = refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDateTime(LocalDateTime.now().plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME - 1L))
                .build()).getRefreshToken() + "haha";

        // when // then
        assertThatThrownBy(() -> authValidationService.verifyRefreshToken(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid token");
    }

    @Test
    @DisplayName("리프레시토큰이 만료되었기 때문에, verify에 실패한다")
    void failVerifyRefreshTokenBecauseRefreshTokenIsExpired() throws Exception {
        // given;
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build());
        Long userId = user.getId();

        String token = refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDateTime(LocalDateTime.now().minusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .build()).getRefreshToken();

        // when // then
        assertThatThrownBy(() -> authValidationService.verifyRefreshToken(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RefreshToken was expired");
    }

}