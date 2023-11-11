package com.example.HardBoard.api.controller.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthRemadeTokenRequest {
    @NotBlank
    private String refreshToken;

    @Builder
    public AuthRemadeTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
