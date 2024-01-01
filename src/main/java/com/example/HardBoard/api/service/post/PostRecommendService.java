package com.example.HardBoard.api.service.post;

import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.post.PostRecommend;
import com.example.HardBoard.domain.post.PostRecommendRepository;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostRecommendService {
    private final PostRecommendRepository postRecommendRepository;
    private final PostRepository postRepository;

    public Long countPostRecommends(Long postId) {
        return postRecommendRepository.countByPostId(postId);
    }

    public Long recommendPost(Long postId, User user) {
        if(postRecommendRepository.existsByUserIdAndPostId(user.getId(), postId))
            throw new IllegalArgumentException("Can't duplicate recommend same post");
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"));
        postRecommendRepository.save(
                PostRecommend.create(user, post));
        return post.getCntRecommends();
    }

    public Long cancelRecommendPost(Long postId, User user) {
        if(postRecommendRepository.existsByUserIdAndPostId(user.getId(), postId) == false)
            throw new IllegalArgumentException("Didn't recommend it");
        postRecommendRepository.deleteByUserIdAndPostId(user.getId(), postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"));
        post.cancelRecommend();

        return post.getCntRecommends();
    }
}
