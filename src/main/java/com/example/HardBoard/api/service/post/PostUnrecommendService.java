package com.example.HardBoard.api.service.post;

import com.example.HardBoard.domain.post.PostRecommend;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.post.PostUnrecommend;
import com.example.HardBoard.domain.post.PostUnrecommendRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostUnrecommendService {
    private final PostUnrecommendRepository postUnrecommendRepository;
    private final PostRepository postRepository;

    public Long countPostUnrecommends(Long postId) {
        return postUnrecommendRepository.countByPostId(postId);
    }

    public Long unrecommendPost(Long postId, User user) {
        if(postUnrecommendRepository.existsByUserIdAndPostId(user.getId(), postId)) throw new IllegalArgumentException("Can't duplicate unrecommend same post");
        postUnrecommendRepository.save(
                PostUnrecommend.builder()
                        .post(postRepository.findById(postId).orElseThrow(() ->
                                new IllegalArgumentException("Invalid postId")))
                        .user(user)
                        .build());
        return postUnrecommendRepository.countByPostId(postId);
    }

    public Long cancelUnrecommendPost(Long postId, User user) {
        if(postUnrecommendRepository.existsByUserIdAndPostId(user.getId(), postId) == false) throw new IllegalArgumentException("Didn't unrecommend it");
        postUnrecommendRepository.deleteByUserIdAndPostId(user.getId(), postId);
        return postUnrecommendRepository.countByPostId(postId);
    }
}
