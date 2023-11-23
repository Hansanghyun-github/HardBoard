package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.api.service.comment.request.CommentCreateServiceRequest;
import com.example.HardBoard.api.service.comment.request.CommentEditServiceRequest;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.domain.comment.Comment;
import com.example.HardBoard.domain.comment.CommentRepository;
import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.user.User;
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

    public void validateComment(Long commentId, User user) {
        if(commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"))
                .getUser().equals(user) == false) throw new IllegalArgumentException("Can't control other user's comment");
    }

    public CommentResponse editComment(CommentEditServiceRequest request) {
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"));
        comment.editContents(request.getContents());
        return CommentResponse.of(comment,
                commentRecommendService.countCommentRecommends(request.getId()),
                commentUnrecommendService.countCommentUnrecommends(request.getId()));
    }

    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"))
                .delete();
        // 댓글은 계층형 구조라, 부모 댓글이 사라지면 안되서 직접 delete하지 않았다.
        // TODO delete 세팅은 해줬는데, 이렇게 되면 더미 데이터가 계속 쌓일 것이다 -> 어떻게 처리해야 하나
    }
}
