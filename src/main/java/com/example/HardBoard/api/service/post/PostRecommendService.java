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

    public Long recommendPost(Long postId, User user) {
        if(postRecommendRepository.existsByUserIdAndPostId(user.getId(), postId)) throw new IllegalArgumentException("Can't duplicate recommend same post");
        postRecommendRepository.save(
                PostRecommend.builder()
                        .post(postRepository.findById(postId).orElseThrow(() ->
                                new IllegalArgumentException("Invalid postId")))
                        .user(user)
                        .build());
        return postRecommendRepository.countByPostId(postId);
    }

    public Long cancelRecommendPost(Long postId, User user) {
        if(postRecommendRepository.existsByUserIdAndPostId(user.getId(), postId) == false) throw new IllegalArgumentException("Didn't recommend it");
        postRecommendRepository.deleteByUserIdAndPostId(user.getId(), postId);
        return postRecommendRepository.countByPostId(postId);
    }
}
