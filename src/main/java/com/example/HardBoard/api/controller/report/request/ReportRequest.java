package com.example.HardBoard.api.controller.report.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReportRequest {
    @NotBlank(message = "사유는 필수입니다")
    private String comments;

    @Builder
    public ReportRequest(String comments) {
        this.comments = comments;
    }
}
