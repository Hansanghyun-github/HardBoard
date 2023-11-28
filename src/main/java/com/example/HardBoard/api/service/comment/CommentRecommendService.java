package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.domain.comment.CommentRecommend;
import com.example.HardBoard.domain.comment.CommentRecommendRepository;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentRecommendService {
    private final CommentRecommendRepository commentRecommendRepository;
    private final CommentRepository commentRepository;
    public Long countCommentRecommends(Long id) {
        return commentRecommendRepository.countByCommentId(id);
    }

    public Long recommendComment(Long commentId, User user) {
        if(commentRecommendRepository.existsByUserIdAndCommentId(user.getId(), commentId)) throw new IllegalArgumentException("Can't duplicate recommend same comment");
        commentRecommendRepository.save(
                CommentRecommend.builder()
                        .comment(commentRepository.findById(commentId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId")))
                        .user(user)
                        .build());
        return commentRecommendRepository.countByCommentId(commentId);
    }

    public Long cancelRecommendComment(Long commentId, User user) {
        if(commentRecommendRepository.existsByUserIdAndCommentId(user.getId(), commentId) == false) throw new IllegalArgumentException("Didn't recommend it");
        commentRecommendRepository.deleteByUserIdAndCommentId(user.getId(), commentId);
        return commentRecommendRepository.countByCommentId(commentId);
    }
}
