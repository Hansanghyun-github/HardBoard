package com.example.HardBoard.api.service.post;

import com.example.HardBoard.domain.post.*;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostUnrecommendService {
    private final PostUnrecommendRepository postUnrecommendRepository;
    private final PostRepository postRepository;

    public Long countPostUnrecommends(Long postId) {
        return postUnrecommendRepository.countByPostId(postId);
    }

    public Long unrecommendPost(Long postId, User user) {
        if(postUnrecommendRepository.existsByUserIdAndPostId(user.getId(), postId))
            throw new IllegalArgumentException("Can't duplicate unrecommend same post");
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("Invalid postId"));
        postUnrecommendRepository.save(
                PostUnrecommend.create(user, post));

        return post.getCntUnrecommends();
    }

    public Long cancelUnrecommendPost(Long postId, User user) {
        if(postUnrecommendRepository.existsByUserIdAndPostId(user.getId(), postId) == false)
            throw new IllegalArgumentException("Didn't unrecommend it");
        postUnrecommendRepository.deleteByUserIdAndPostId(user.getId(), postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"));
        post.cancelUnrecommend();

        return post.getCntUnrecommends();
    }
}
