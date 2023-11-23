package com.example.HardBoard.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {
    Long countByCommentId(Long commentId);
    Optional<CommentRecommend> findByCommentIdAndUserId(Long com, Long userId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
