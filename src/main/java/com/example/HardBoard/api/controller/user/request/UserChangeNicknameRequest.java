package com.example.HardBoard.api.controller.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class UserChangeNicknameRequest {
    @NotBlank
    private String prevNickname;

    @NotBlank
    private String newNickname;

    @Builder
    private UserChangeNicknameRequest(String prevNickname, String newNickname) {
        this.prevNickname = prevNickname;
        this.newNickname = newNickname;
    }
}
