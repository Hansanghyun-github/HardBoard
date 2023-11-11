package com.example.HardBoard.api.controller.auth.request;

import com.example.HardBoard.api.service.auth.request.MailSendServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class AuthEmailSendRequest {
    @NotBlank @Email
    private String email;

    public MailSendServiceRequest toServiceRequest(){
        return MailSendServiceRequest.builder()
                .to(email)
                .build();
    }
}
