package com.example.HardBoard.api.controller.comment;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.comment.request.CommentCreateRequest;
import com.example.HardBoard.api.controller.comment.request.CommentEditRequest;
import com.example.HardBoard.api.service.comment.CommentRecommendService;
import com.example.HardBoard.api.service.comment.CommentService;
import com.example.HardBoard.api.service.comment.CommentUnrecommendService;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final CommentRecommendService commentRecommendService;
    private final CommentUnrecommendService commentUnrecommendService;

    @PostMapping("/comments/{postId}")
    public ApiResponse<CommentResponse> createComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ){
        return ApiResponse.ok(commentService.createComment(request
                .toServiceRequest(principal.getUser(), postId)));
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> editComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentEditRequest request
    ){
        // TODO validate comment is authenticated user's comment
        return null;
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId
    ){
        // TODO validate comment is authenticated user's comment
        // TODO not delete, only check comment's field(isDeleted), to maintain hierarchy
        return null;
    }

    @PostMapping("/comments/{commentId}/recommend")
    public ApiResponse<Long> recommentComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId
    ){
        return null;
    }

    @DeleteMapping("/comments/{commentId}/recommend")
    public ApiResponse<Long> cancelRecommendComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId
    ){
        return null;
    }

    @PostMapping("/comments/{commentId}/unrecommend")
    public ApiResponse<Long> unrecommentComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId
    ){
        return null;
    }

    @DeleteMapping("/comments/{commentId}/unrecommend")
    public ApiResponse<Long> cancelUnrecommendComment(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long commentId
    ){
        return null;
    }
}
