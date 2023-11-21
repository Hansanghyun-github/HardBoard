package com.example.HardBoard.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRecommendRepository extends JpaRepository<PostRecommend, Long> {
    public void deleteByUserIdAndPostId(Long userId, Long postId);

    public Long countByPostId(Long postId);

    public Optional<PostRecommend> findByUserIdAndPostId(Long userId, Long postId);
}
