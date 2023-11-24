package com.example.HardBoard.api.controller.block.request;

import com.example.HardBoard.api.service.block.request.BlockServiceRequest;
import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockRequest {
    private String comments;

    @Builder
    public BlockRequest(String comments) {
        this.comments = comments;
    }

    public BlockServiceRequest toServiceRequest(User user, Long blockUserId) {
        return BlockServiceRequest.builder()
                .comments(comments)
                .user(user)
                .blockUserId(blockUserId)
                .build();
    }
}
