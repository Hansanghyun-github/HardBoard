package com.example.HardBoard.api.service.report.request;

import com.example.HardBoard.domain.report.TargetStatus;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportServiceRequest {
    private String comments;
    private Long targetId;
    private User user;
    private TargetStatus status;

    @Builder
    public ReportServiceRequest(String comments, Long targetId, User user, TargetStatus status) {
        this.comments = comments;
        this.targetId = targetId;
        this.user = user;
        this.status = status;
    }
}
