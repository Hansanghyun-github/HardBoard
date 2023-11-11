package com.example.HardBoard.domain.refreshToken;

import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class RefreshTokenRepositoryTest {
    @Autowired UserRepository userRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시토큰 번호로 리프레시토큰 객체를 찾는다")
    void findByRefreshToken() throws Exception {
        // given
        User user = User.builder()
                .email("gsdk@gkds")
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        String tokenNum = UUID.randomUUID().toString();
        LocalDateTime dateTime = LocalDateTime.now()
                .plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(tokenNum)
                .expirationDate(dateTime)
                .build();
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> token = refreshTokenRepository.findByRefreshToken(tokenNum);

        // then
        assertThat(token.isPresent()).isTrue();
        assertThat(token.get().getExpirationDate()).isEqualTo(dateTime);
        assertThat(token.get().getRefreshToken()).isEqualTo(tokenNum);
        assertThat(token.get().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("유저id로 리프레시토큰이 존재 하는지 찾는다")
    void existsByUserId() throws Exception {
        // given
        User user = User.builder()
                .email("gsdk@gkds")
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        Long userId = user.getId();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now()
                        .plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .build();
        refreshTokenRepository.save(refreshToken);

        // when // then
        assertThat(refreshTokenRepository.existsByUserId(userId))
                .isTrue();
        assertThat(refreshTokenRepository.existsByUserId(1L+userId))
                .isFalse();
    }

    @Test
    @DisplayName("유저id를 이용해 리프레시토큰 객체를 삭제한다")
    void deleteByUserId() throws Exception {
        // given
        User user = User.builder()
                .email("gsdk@gkds")
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        Long userId = user.getId();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now()
                        .plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .build();
        refreshTokenRepository.save(refreshToken);


        // when
        refreshTokenRepository.deleteByUserId(userId);

        // then
        assertThat(refreshTokenRepository.existsByUserId(userId)).isFalse();
    }
}