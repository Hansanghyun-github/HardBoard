package com.example.HardBoard.api.service.auth.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class AuthLoginServiceRequest {
    private String email;
    private String password;
}
