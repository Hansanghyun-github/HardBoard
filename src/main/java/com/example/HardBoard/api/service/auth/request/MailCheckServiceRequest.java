package com.example.HardBoard.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailCheckServiceRequest {
    private String email;
    private String authNumber;

    @Builder
    private MailCheckServiceRequest(String email, String authNumber) {
        this.email = email;
        this.authNumber = authNumber;
    }
}
