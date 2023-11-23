package com.example.HardBoard.api.service.post;

import com.example.HardBoard.domain.post.PostRecommend;
import com.example.HardBoard.domain.post.PostRecommendRepository;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostRecommendService {
    private final PostRecommendRepository postRecommendRepository;
    private final PostRepository postRepository;

    public Long countPostRecommends(Long postId) {
        return postRecommendRepository.countByPostId(postId);
    }

    // TODO 추천, 비추천 중복 안되게 막아야 함 (Post, Comment 둘 다)
    public Long recommendPost(Long postId, User user) {
        postRecommendRepository.save(
                PostRecommend.builder()
                        .post(postRepository.findById(postId).orElseThrow(() ->
                                new IllegalArgumentException("Invalid postId")))
                        .user(user)
                        .build());
        return postRecommendRepository.countByPostId(postId);
    }

    public Long cancelRecommendPost(Long postId, User user) {
        // TODO userId or postId가 invalid 하다면 어떻게 해야 하나 - 중복 쿼리 줄여야 함?
        postRecommendRepository.deleteByUserIdAndPostId(user.getId(), postId);
        return postRecommendRepository.countByPostId(postId);
    }
}
