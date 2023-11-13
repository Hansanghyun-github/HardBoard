package com.example.HardBoard.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserChangePasswordServiceRequest {
    private String prevPassword;
    private String newPassword;

    @Builder
    public UserChangePasswordServiceRequest(String prevPassword, String newPassword) {
        this.prevPassword = prevPassword;
        this.newPassword = newPassword;
    }
}
