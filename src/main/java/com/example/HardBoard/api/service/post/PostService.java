package com.example.HardBoard.api.service.post;

import com.example.HardBoard.api.service.post.request.PostCreateServiceRequest;
import com.example.HardBoard.api.service.post.request.PostEditServiceRequest;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostRecommendService postRecommendService;
    private final PostUnrecommendService postUnrecommendService;

    public PostResponse createPost(PostCreateServiceRequest request) {
        Post post = postRepository.save(
                Post.create(request.getTitle(),
                        request.getContents(),
                        request.getCategory(),
                        request.getUser()));
        return PostResponse.of(post, 0L, 0L);
    }

    public void validatePost(Long postId, User user) {
        if(postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"))
                .getUser().equals(user) == false)
            throw new IllegalArgumentException("Post's user is different with authenticated user");
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public PostResponse editPost(PostEditServiceRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId"));
        post.edit(request.getTitle(), request.getContents());
        return PostResponse.of(post,
                postRecommendService.countPostRecommends(request.getPostId()),
                postUnrecommendService.countPostUnrecommends(request.getPostId()));
    }
}
