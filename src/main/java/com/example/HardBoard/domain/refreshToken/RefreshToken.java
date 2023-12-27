package com.example.HardBoard.domain.refreshToken;

import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_tokens")
@ToString(exclude = {"user"})
public class RefreshToken {
    @Id @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "expiration_date_time", nullable = false)
    private LocalDateTime expirationDateTime;

    @Builder
    private RefreshToken(User user, String refreshToken, LocalDateTime expirationDateTime) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expirationDateTime = expirationDateTime;
    }

    public static RefreshToken create(User user, LocalDateTime dateTime){
        return RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDateTime(dateTime
                        .plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .build();
    }

    public void refreshTokenRotation(LocalDateTime dateTime){
        this.refreshToken = UUID.randomUUID().toString();
        this.expirationDateTime = dateTime.plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public boolean isExpired(LocalDateTime dateTime){ return this.expirationDateTime.isBefore(dateTime); }
}
