package com.example.HardBoard.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthPasswordChangeServiceRequest {
    private String email;
    private String prevPassword;
    private String newPassword;
}
