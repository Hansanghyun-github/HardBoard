package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.api.service.comment.request.CommentCreateServiceRequest;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.domain.comment.Comment;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final CommentRecommendService commentRecommendService;
    private final CommentUnrecommendService commentUnrecommendService;

    public CommentResponse createComment(CommentCreateServiceRequest request) {
        Comment comment = commentRepository.save(
                Comment.builder()
                        .contents(request.getContents())
                        .user(request.getUser())
                        .post(postRepository.findById(request.getPostId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid postId")))
                        .build());
        // TODO -1이 아니라 다른 걸로 구별해야 함 - 가독성 쓰레기
        if(request.getParentCommentId().equals(-1L)) // TODO separate other method to OOP
            comment.setParent();
        else comment.setParent(
                commentRepository.findById(request.getParentCommentId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment id")));
        return CommentResponse.of(
                comment,
                0L,
                0L);
    }
}
