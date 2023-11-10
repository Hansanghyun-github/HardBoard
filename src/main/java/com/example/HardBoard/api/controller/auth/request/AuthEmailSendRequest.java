package com.example.HardBoard.api.controller.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class AuthEmailSendRequest {
    @NotBlank @Email
    private String email;
}
