package com.example.HardBoard.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostUnrecommendRepository extends JpaRepository<PostUnrecommend, Long> {
    public boolean existsByUserIdAndPostId(Long userId, Long postId);
    public void deleteByUserIdAndPostId(Long userId, Long postId);

    public Long countByPostId(Long postId);

    public Optional<PostUnrecommend> findByUserIdAndPostId(Long userId, Long postId);
}
