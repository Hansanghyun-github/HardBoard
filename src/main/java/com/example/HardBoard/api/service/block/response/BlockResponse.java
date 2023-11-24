package com.example.HardBoard.api.service.block.response;

import com.example.HardBoard.domain.block.Block;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockResponse {
    private Long blockId;
    private String comments;
    private Long userId;
    private Long blockUserId;

    @Builder
    public BlockResponse(Long blockId, String comments, Long userId, Long blockUserId) {
        this.blockId = blockId;
        this.comments = comments;
        this.userId = userId;
        this.blockUserId = blockUserId;
    }

    public static BlockResponse of(Block block) {
        return BlockResponse.builder()
                .blockId(block.getId())
                .comments(block.getComments())
                .userId(block.getUser().getId())
                .blockUserId(block.getBlockUser().getId())
                .build();
    }
}
