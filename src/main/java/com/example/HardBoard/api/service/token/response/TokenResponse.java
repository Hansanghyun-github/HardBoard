package com.example.HardBoard.api.service.token.response;

import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class TokenResponse {
    String accessToken;
    String refreshToken;

    @Builder
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
