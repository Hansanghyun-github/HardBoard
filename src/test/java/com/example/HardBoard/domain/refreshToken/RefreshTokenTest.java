package com.example.HardBoard.domain.refreshToken;

import com.example.HardBoard.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
}