package com.example.HardBoard.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentUnrecommendRepository extends JpaRepository<CommentUnrecommend, Long> {
    Optional<CommentUnrecommend> findByCommentIdAndUserId(Long com, Long userId);

    Long countByCommentId(Long commentId);
}
