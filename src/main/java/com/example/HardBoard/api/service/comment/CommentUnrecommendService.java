package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.comment.CommentUnrecommend;
import com.example.HardBoard.domain.comment.CommentUnrecommendRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentUnrecommendService {
    private final CommentUnrecommendRepository commentUnrecommendRepository;
    private final CommentRepository commentRepository;
    public Long countCommentUnrecommends(Long id) {
        return commentUnrecommendRepository.countByCommentId(id);
    }

    public Long unrecommendComment(Long commentId, User user) {
        commentUnrecommendRepository.save(
                CommentUnrecommend.builder()
                        .comment(commentRepository.findById(commentId)
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid commentId")))
                        .user(user)
                        .build());
        return commentUnrecommendRepository.countByCommentId(commentId);
    }

    public Long cancelUnrecommendComment(Long commentId, User user) {
        commentUnrecommendRepository.deleteByUserIdAndCommentId(user.getId(), commentId);
        return commentUnrecommendRepository.countByCommentId(commentId);
    }
}
