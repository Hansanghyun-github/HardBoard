package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.domain.comment.Comment;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.comment.CommentUnrecommend;
import com.example.HardBoard.domain.comment.CommentUnrecommendRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentUnrecommendService {
    private final CommentUnrecommendRepository commentUnrecommendRepository;
    private final CommentRepository commentRepository;
    public Long countCommentUnrecommends(Long id) {
        return commentUnrecommendRepository.countByCommentId(id);
    }

    public Long unrecommendComment(Long commentId, User user) {
        if(commentUnrecommendRepository.existsByUserIdAndCommentId(user.getId(), commentId)) throw new IllegalArgumentException("Can't duplicate unrecommend same comment");

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"));
        commentUnrecommendRepository.save(
                CommentUnrecommend.create(user, comment));

        return comment.getCntUnrecommends();
    }

    public Long cancelUnrecommendComment(Long commentId, User user) {
        if(commentUnrecommendRepository.existsByUserIdAndCommentId(user.getId(), commentId) == false) throw new IllegalArgumentException("Didn't unrecommend it");
        commentUnrecommendRepository.deleteByUserIdAndCommentId(user.getId(), commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"));
        comment.cancelUnrecommend();

        return comment.getCntUnrecommends();
    }
}
