package com.example.HardBoard.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthPasswordChangeServiceRequest {
    private String email;
    private String prevPassword;
    private String newPassword;

    @Builder
    public AuthPasswordChangeServiceRequest(String email, String prevPassword, String newPassword) {
        this.email = email;
        this.prevPassword = prevPassword;
        this.newPassword = newPassword;
    }
}
