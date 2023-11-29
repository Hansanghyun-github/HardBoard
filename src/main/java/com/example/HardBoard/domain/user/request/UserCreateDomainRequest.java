package com.example.HardBoard.domain.user.request;

import lombok.*;

@Getter
@NoArgsConstructor
public class UserCreateDomainRequest {
    private String email;
    private String password;
    private String nickname;

    @Builder
    public UserCreateDomainRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
