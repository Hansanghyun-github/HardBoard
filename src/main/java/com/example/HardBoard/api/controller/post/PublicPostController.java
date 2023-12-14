package com.example.HardBoard.api.controller.post;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.service.comment.PublicCommentService;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.api.service.post.PublicPostService;
import com.example.HardBoard.api.service.post.response.PostCommentResponse;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.post.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicPostController {
    private final PublicPostService publicPostService;
    private final PublicCommentService publicCommentService;

    @GetMapping("/public/posts")
    public ApiResponse<List<PostResponse>> getPostList(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(name = "category", defaultValue = "All") String category,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(publicPostService.getPostList(principal,
                Category.lookup(category),
                page - 1));
    }

    @GetMapping("/public/users/posts/{userId}")
    public ApiResponse<List<PostResponse>> getPostListOfUser(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(publicPostService.getPostListByUserId(userId, page - 1));
    }

    @GetMapping("/public/posts/{postId}")
    public ApiResponse<PostCommentResponse> getPostOfPostId(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long postId
    ){
        // TODO 조회 수 1 증가

        int page = publicCommentService.countLastPageOfPost(postId);
        return ApiResponse.ok(PostCommentResponse.of(publicPostService.findById(postId),
                publicCommentService.getCommentListOfPost(postId, principal, page)));
    }

    @GetMapping("/public/posts/day")
    public ApiResponse<List<PostResponse>> getDayBestRecommendPostList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        // TODO 유저와 글 만드는 것 따로 메서드로 구현(LocalDateTime 받아서 원하는 시간에 만들어지도록)
        /*
         * TODO 로그인 됐다면, 차단한 유저 안보여야 함,
         * TODO 로그인 안 했다면, 그냥 다 보여줘야 함
         */
        return ApiResponse.ok(publicPostService.getDayBestRecommendPostList(principal));
    }

    @GetMapping("/public/posts/week")
    public ApiResponse<List<PostResponse>> getWeekBestRecommendPostList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        /*
         * TODO 로그인 됐다면, 차단한 유저 안보여야 함,
         * TODO 로그인 안 했다면, 그냥 다 보여줘야 함
         */
        return ApiResponse.ok(publicPostService.getWeekBestRecommendPostList(principal));
    }

    @GetMapping("/public/posts/month")
    public ApiResponse<List<PostResponse>> getMonthBestRecommendPostList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        /*
         * TODO 로그인 됐다면, 차단한 유저 안보여야 함,
         * TODO 로그인 안 했다면, 그냥 다 보여줘야 함
         */
        return ApiResponse.ok(publicPostService.getMonthBestRecommendPostList(principal));
    }
}
