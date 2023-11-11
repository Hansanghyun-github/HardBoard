package com.example.HardBoard.domain.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class UserCreateDomainRequest {
    private String email;
    private String password;
    private String nickname;
}
