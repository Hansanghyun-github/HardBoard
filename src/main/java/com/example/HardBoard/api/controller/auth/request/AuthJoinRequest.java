package com.example.HardBoard.api.controller.auth.request;

import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthJoinRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String authNumber;

    @Builder
    private AuthJoinRequest(String email, String password, String nickname, String authNumber) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.authNumber = authNumber;
    }

    public UserCreateServiceRequest toUserServiceRequest(){
        return UserCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    public MailCheckServiceRequest toMailCheckRequest(){
        return MailCheckServiceRequest.builder()
                .email(email)
                .authNumber(authNumber)
                .build();
    }

    public MailCheckServiceRequest toMailCheckServiceRequest() {
        return MailCheckServiceRequest.builder()
                .email(this.email)
                .authNumber(this.authNumber)
                .build();
    }
}
