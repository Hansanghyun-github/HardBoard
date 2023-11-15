package com.example.HardBoard.api.controller.auth.request;

import com.example.HardBoard.api.service.auth.request.MailSendServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AuthEmailSendRequest {
    @NotBlank @Email
    private String email;

    @Builder
    private AuthEmailSendRequest(String email) {
        this.email = email;
    }

    public MailSendServiceRequest toServiceRequest(){
        return MailSendServiceRequest.builder()
                .to(email)
                .build();
    }
}
