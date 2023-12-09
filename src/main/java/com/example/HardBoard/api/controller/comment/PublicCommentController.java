package com.example.HardBoard.api.controller.comment;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.service.comment.CommentRecommendService;
import com.example.HardBoard.api.service.comment.CommentService;
import com.example.HardBoard.api.service.comment.CommentUnrecommendService;
import com.example.HardBoard.api.service.comment.PublicCommentService;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.api.service.post.PostService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.auth.PrincipalDetails;
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
public class PublicCommentController {
    private static final int LAST_PAGE = 0;
    private final PublicCommentService publicCommentService;
    private final PostService postService;
    private final UserService userService;

    // 따로 댓글 리스트를 요청할 때 호출 -> 굳이 마지막 페이지 알 필요 없다.
    @GetMapping("/public/comments/post/{postId}") // mapping 겹치면 안되서 일부러 /post/를 넣었다.
    public ApiResponse<List<CommentResponse>> getCommentListOfPost(
            @AuthenticationPrincipal PrincipalDetails principal, // TODO 여기서 주입하지 말고, 서비스 단에서 확인하자
            @PathVariable Long postId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        // TODO 깃허브의 다른 서비스에서는 어떻게 이걸 구현해놨을까?
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        postService.validatePost(postId);
        return ApiResponse.ok(publicCommentService.getCommentListOfPost(postId,
                principal,
                page - 1)); // JPA page는 0부터 시작이라서 -1
    }

    @GetMapping("/public/comments/{userId}")
    public ApiResponse<List<CommentResponse>> getCommentListOfUser( // TODO CommentResponse가 아닌 새로운 Response가 필요하다
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        userService.validateUser(userId);
        return ApiResponse.ok(publicCommentService.getCommentListOfUser(userId, page - 1));
    }
}
