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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final CommentRecommendService commentRecommendService;
    private final CommentUnrecommendService commentUnrecommendService;

    public CommentResponse createComment(CommentCreateServiceRequest request) {
        Comment comment = commentRepository.save(
                Comment.create(
                        request.getContents(),
                        postRepository.findById(request.getPostId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid postId")),
                        request.getUser()));
        // TODO -1이 아니라 다른 걸로 구별해야 함 - 가독성 쓰레기
        if(request.getParentCommentId().equals(-1L)) // TODO separate other method to OOP
            comment.setParent();
        else comment.setParent(
                commentRepository.findById(request.getParentCommentId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment id")));
        return CommentResponse.of(
                comment);
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
        return CommentResponse.of(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commentId"))
                .delete();
        // 댓글은 계층형 구조라, 부모 댓글이 사라지면 안되서 직접 delete하지 않았다.
        // TODO 루트댓글이라면 삭제(하위 댓글 없을 때 -> 동시성 문제), 아니라면 보이지 않게 세팅
    }

    public Long countCommentsOfPostId(Long postId){
        return commentRepository.countByPostId(postId);
    }
}
