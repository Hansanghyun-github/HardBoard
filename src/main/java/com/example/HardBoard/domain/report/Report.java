package com.example.HardBoard.domain.report;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Long targetId;

    @Enumerated(value = EnumType.STRING)
    private ReportStatus status;

    @Builder
    public Report(String comments, User user, Long targetId, ReportStatus status) {
        this.comments = comments;
        this.user = user;
        this.targetId = targetId;
        this.status = status;
    }
}
