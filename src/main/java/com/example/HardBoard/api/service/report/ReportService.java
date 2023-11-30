package com.example.HardBoard.api.service.report;

import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.api.service.report.request.ReportServiceRequest;
import com.example.HardBoard.api.service.report.response.ReportResponse;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.report.Report;
import com.example.HardBoard.domain.report.ReportRepository;
import com.example.HardBoard.domain.report.TargetStatus;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void createReport(ReportServiceRequest request) {
        reportRepository.save(
                Report.builder()
                        .user(request.getUser())
                        .comments(request.getComments())
                        .targetId(request.getTargetId())
                        .status(request.getStatus())
                        .build()
        );
    }

    public void validateTarget(TargetStatus status, Long targetId) {
        if(status == TargetStatus.POST && !postRepository.existsById(targetId))
            throw new IllegalArgumentException("Invalid postId");
        else if(status == TargetStatus.COMMENT && !commentRepository.existsById(targetId))
            throw new IllegalArgumentException("Invalid commentId");
    }

    public void validateTarget(Long reportId, User user) {
        if(reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reportId")).getUser().equals(user) == false)
            throw new IllegalArgumentException("Can't delete other user's report");
    }

    public void cancelReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    public List<ReportResponse> getBlockList(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 20,
                Sort.by(Sort.Direction.DESC, "createdDateTime"));
        return reportRepository.findByUserId(userId, pageRequest)
                .map(ReportResponse::of)
                .getContent();
    }
}
