package com.example.HardBoard.api.service.user.request;

import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateServiceRequest {
    private String email;
    private String password;
    private String nickname;

    @Builder
    private UserCreateServiceRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public UserCreateDomainRequest toDomainRequest(){
        return UserCreateDomainRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
