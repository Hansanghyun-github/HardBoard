package com.example.HardBoard.domain.comment;

import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.domain.comment.customRepository.CommentRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    Long countByPostId(Long postId);
}
