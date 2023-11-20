package com.example.HardBoard.api.service.token.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TokenResponse {
    String accessToken;
    String refreshToken;
}
