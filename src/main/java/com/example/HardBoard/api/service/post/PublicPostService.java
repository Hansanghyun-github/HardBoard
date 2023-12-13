package com.example.HardBoard.api.service.post;

import com.example.HardBoard.api.service.block.BlockService;
import com.example.HardBoard.api.service.post.response.PostResponse;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.PostRepository;
import com.example.HardBoard.domain.post.PublicPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicPostService {
    private static final int PAGE_SIZE = 20;

    private final PostRepository postRepository;
    private final PublicPostRepository publicPostRepository;
    private final BlockService blockService;
    private final UserService userService;

    private final PostRecommendService postRecommendService;
    private final PostUnrecommendService postUnrecommendService;

    public List<PostResponse> getPostList(PrincipalDetails principal, Category category, int page) {
        List<Long> blockList = blockService.getBlockUserIdList(principal);

        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        return publicPostRepository.findByCategoryWithoutBlockUser(category, blockList, pageRequest)
                .map(post -> PostResponse.of(post,
                        postRecommendService.countPostRecommends(post.getId()),
                        postUnrecommendService.countPostUnrecommends(post.getId())))
                .getContent();
    }

    public List<PostResponse> getPostListByUserId(Long userId, int page) {
        userService.validateUser(userId);
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        return publicPostRepository.findByUserId(userId, pageRequest)
                .map(post -> PostResponse.of(post,
                        postRecommendService.countPostRecommends(post.getId()),
                        postUnrecommendService.countPostUnrecommends(post.getId())))
                .getContent();
    }

    public PostResponse findById(Long postId) {
        return PostResponse.of(postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid postId")),
                postRecommendService.countPostRecommends(postId),
                postUnrecommendService.countPostUnrecommends(postId));
    }
}
