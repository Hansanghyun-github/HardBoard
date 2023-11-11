package com.example.HardBoard.domain.refreshToken;

import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenTest {
    @Test
    @DisplayName("리프레시토큰 로테이션 된다")
    void refreshTokenRotation() throws Exception {
        // given
        RefreshToken token = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(User.builder().build())
                .expirationDate(LocalDateTime.now())
                .build();
        RefreshToken prevToken = RefreshToken.builder()
                .refreshToken(token.getRefreshToken())
                .user(token.getUser())
                .expirationDate(token.getExpirationDate())
                .build();
        LocalDateTime dateTime = LocalDateTime.now();

        // when
        token.refreshTokenRotation(dateTime);

        // then
        assertThat(token.getRefreshToken()).isNotEqualTo(prevToken.getRefreshToken());
        assertThat(token.getExpirationDate()).isEqualTo(dateTime.plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME));
    }

    @Test
    @DisplayName("리프레시토큰이 만료되지 않았다")
    void notExpiredRefreshToken() throws Exception {
        // given
        RefreshToken token = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now())
                .build();
        LocalDateTime curDateTime = LocalDateTime.now().minusSeconds(10L);

        // when // then
        assertThat(token.isExpired(curDateTime)).isEqualTo(false);
    }

    @Test
    @DisplayName("리프레시토큰이 만료되었다")
    void expiredRefreshToken() throws Exception {
        // given
        RefreshToken token = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now())
                .build();
        LocalDateTime curDateTime = LocalDateTime.now().plusSeconds(10L);

        // when // then
        assertThat(token.isExpired(curDateTime)).isEqualTo(true);
    }
}