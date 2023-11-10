package com.example.HardBoard.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailSendServiceRequest {
    private String to;

    @Builder
    private MailSendServiceRequest(String to) {
        this.to = to;
    }
}
