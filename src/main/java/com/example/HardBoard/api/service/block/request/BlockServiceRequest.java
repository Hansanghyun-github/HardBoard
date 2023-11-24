package com.example.HardBoard.api.service.block.request;

import com.example.HardBoard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockServiceRequest {
    private String comments;
    private User user;
    private Long blockUserId;

    @Builder
    public BlockServiceRequest(String comments, User user, Long blockUserId) {
        this.comments = comments;
        this.user = user;
        this.blockUserId = blockUserId;
    }
}
