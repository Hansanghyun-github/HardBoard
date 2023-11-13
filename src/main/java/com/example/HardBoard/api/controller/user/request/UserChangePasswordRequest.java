package com.example.HardBoard.api.controller.user.request;

import com.example.HardBoard.api.service.user.request.UserChangePasswordServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class UserChangePasswordRequest {
    @NotBlank
    private String prevPassword;

    @NotBlank
    private String newPassword;

    @Builder
    private UserChangePasswordRequest(String prevPassword, String newPassword) {
        this.prevPassword = prevPassword;
        this.newPassword = newPassword;
    }

    public UserChangePasswordServiceRequest toServiceRequest(){
        return UserChangePasswordServiceRequest.builder()
                .prevPassword(prevPassword)
                .newPassword(newPassword)
                .build();
    }
}
