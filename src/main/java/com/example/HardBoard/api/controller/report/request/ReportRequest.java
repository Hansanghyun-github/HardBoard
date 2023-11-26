package com.example.HardBoard.api.controller.report.request;

import com.example.HardBoard.api.service.report.request.ReportServiceRequest;
import com.example.HardBoard.domain.report.TargetStatus;
import com.example.HardBoard.domain.user.User;
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

    public ReportServiceRequest toServiceRequest(User user, Long postId, TargetStatus status) {
        return ReportServiceRequest.builder()
                .comments(comments)
                .user(user)
                .targetId(postId)
                .status(status)
                .build();
    }
}
