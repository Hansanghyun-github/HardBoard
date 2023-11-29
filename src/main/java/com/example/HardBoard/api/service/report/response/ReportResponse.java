package com.example.HardBoard.api.service.report.response;

import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.domain.report.Report;
import com.example.HardBoard.domain.report.TargetStatus;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReportResponse {
    private Long id;

    private String comments;

    private Long userId;

    private Long targetId;

    private TargetStatus status;

    private LocalDateTime createdDateTime;

    @Builder
    public ReportResponse(Long id, String comments, Long userId, Long targetId, TargetStatus status, LocalDateTime createdDateTime) {
        this.id = id;
        this.comments = comments;
        this.userId = userId;
        this.targetId = targetId;
        this.status = status;
        this.createdDateTime = createdDateTime;
    }

    public static ReportResponse of(Report report){
        return ReportResponse.builder()
                .id(report.getId())
                .comments(report.getComments())
                .userId(report.getUser().getId())
                .targetId(report.getTargetId())
                .status(report.getStatus())
                .createdDateTime(report.getCreatedDateTime())
                .build();
    }
}
