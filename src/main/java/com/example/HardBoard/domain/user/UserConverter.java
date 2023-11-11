package com.example.HardBoard.domain.user;

import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {
    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserCreateDomainRequest request){
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.ROLE_USER)
                .build();
    }
}
