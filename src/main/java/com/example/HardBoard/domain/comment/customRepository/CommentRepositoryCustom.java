package com.example.HardBoard.domain.comment.customRepository;

import com.example.HardBoard.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepositoryCustom {
    Page<Comment> findByPostId(Long postId, List<Long> blockUserIdList, Pageable pageable);
}
