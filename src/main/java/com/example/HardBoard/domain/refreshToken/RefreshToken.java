package com.example.HardBoard.domain.refreshToken;

import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
    @JoinColumn(name = "user_id")
    @Column(nullable = false, unique = true)
    private User user;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Builder
    private RefreshToken(User user, String refreshToken, LocalDateTime expirationDate) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }

    public void refreshTokenRotation(LocalDateTime dateTime){
        this.refreshToken = UUID.randomUUID().toString();
        this.expirationDate = dateTime.plusSeconds((JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME));
    }
}
