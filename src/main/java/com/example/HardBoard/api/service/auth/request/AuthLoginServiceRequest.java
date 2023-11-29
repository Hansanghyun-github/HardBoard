package com.example.HardBoard.api.service.auth.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginServiceRequest {
    private String email;
    private String password;

    @Builder
    public AuthLoginServiceRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
