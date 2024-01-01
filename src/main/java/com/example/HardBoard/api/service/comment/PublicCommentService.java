package com.example.HardBoard.api.service.comment;

import com.example.HardBoard.api.service.block.BlockService;
import com.example.HardBoard.api.service.comment.response.CommentResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicCommentService {
    private static final int PAGE_SIZE = 20;

    private final CommentRepository commentRepository;
    private final BlockService blockService;

    private final CommentRecommendService commentRecommendService;
    private final CommentUnrecommendService commentUnrecommendService;

    public List<CommentResponse> getCommentListOfPost(Long postId, PrincipalDetails principal, int page) {
        List<Long> blockUserIdList = blockService.getBlockUserIdList(principal);

        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        return commentRepository.findByPostId(postId, blockUserIdList, pageRequest)
                .map(comment -> CommentResponse.of(comment))
                .getContent();
    }

    public List<CommentResponse> getCommentListOfUser(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdDateTime"));
        return commentRepository.findByUserId(userId, pageRequest)
                .map(comment -> CommentResponse.of(comment))
                .getContent();
    }

    public int countLastPageOfPost(Long postId) { // TODO Boundary Unit Test
        return commentRepository.countByPostId(postId).intValue() / PAGE_SIZE;
    }
}
