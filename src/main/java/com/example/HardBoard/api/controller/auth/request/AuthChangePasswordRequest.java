package com.example.HardBoard.api.controller.auth.request;

import com.example.HardBoard.api.service.auth.request.AuthPasswordChangeServiceRequest;
import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthChangePasswordRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String authNumber;

    @NotBlank
    private String prevPassword;

    @NotBlank
    private String newPassword;

    @Builder
    private AuthChangePasswordRequest(String email, String authNumber, String prevPassword, String newPassword) {
        this.email = email;
        this.authNumber = authNumber;
        this.prevPassword = prevPassword;
        this.newPassword = newPassword;
    }

    public MailCheckServiceRequest toMailCheckServiceRequest(){
        return MailCheckServiceRequest.builder()
                .email(email)
                .authNumber(authNumber)
                .build();
    }

    public AuthPasswordChangeServiceRequest toPasswordChangeServiceRequest(){
        return AuthPasswordChangeServiceRequest.builder()
                .email(email)
                .prevPassword(prevPassword)
                .newPassword(newPassword)
                .build();
    }
}
