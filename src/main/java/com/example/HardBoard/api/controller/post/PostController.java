package com.example.HardBoard.api.controller.post;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.post.request.PostCreateRequest;
import com.example.HardBoard.api.controller.post.request.PostEditRequest;
import com.example.HardBoard.api.service.post.PostRecommendService;
import com.example.HardBoard.api.service.post.PostService;
import com.example.HardBoard.api.service.post.PostUnrecommendService;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final PostRecommendService postRecommendService;
    private final PostUnrecommendService postUnrecommendService;
    // TODO recommend/unrecommend도 PostService에서 처리해주는게 깔끔하지 않을까?

    @PostMapping("/posts")
    public ApiResponse<PostResponse> createPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @Valid @RequestBody PostCreateRequest request
            ){
        return ApiResponse.ok(postService
                .createPost(request.toServiceRequest(principal.getUser())));
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deletePost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        postService.validatePost(postId, principal.getUser());
        postService.deletePost(postId);
        return ApiResponse.ok("ok");
    }

    @PostMapping("/posts/{postId}")
    public ApiResponse<PostResponse> editPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody PostEditRequest request
            ){
        postService.validatePost(postId, principal.getUser());
        return ApiResponse.ok(postService.editPost(request.toServiceRequest(postId)));
    }

    @PostMapping("/posts/{postId}/recommend")
    public ApiResponse<Long> recommendPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        // return current recommends
        return ApiResponse.ok(postRecommendService.recommendPost(postId, principal.getUser()));
    }

    @DeleteMapping("/posts/{postId}/recommend")
    public ApiResponse<Long> cancleRecommendPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        // return current recommends
        return ApiResponse.ok(postRecommendService.cancelRecommendPost(postId, principal.getUser()));
    }

    @PostMapping("/posts/{postId}/unrecommend")
    public ApiResponse<Long> unrecommendPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        // return current unrecommends
        return ApiResponse.ok(postUnrecommendService.unrecommendPost(postId, principal.getUser()));
    }

    @DeleteMapping("/posts/{postId}/unrecommend")
    public ApiResponse<Long> cancleUnrecommendPost(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        // return current unrecommends
        return ApiResponse.ok(postUnrecommendService.cancelUnrecommendPost(postId, principal.getUser()));
    }
}
