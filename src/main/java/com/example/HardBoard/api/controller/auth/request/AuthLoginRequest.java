package com.example.HardBoard.api.controller.auth.request;

import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthLoginRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @Builder
    private AuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthLoginServiceRequest toServiceRequest(){ return new AuthLoginServiceRequest(email, password); }
}
