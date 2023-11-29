package com.example.HardBoard.api.service.block.response;

import com.example.HardBoard.domain.block.Block;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class BlockResponse {
    private Long blockId;
    private String comments;
    private Long userId;
    private Long blockUserId;
    private LocalDateTime createdDateTime;

    @Builder
    public BlockResponse(Long blockId, String comments, Long userId, Long blockUserId, LocalDateTime createdDateTime) {
        this.blockId = blockId;
        this.comments = comments;
        this.userId = userId;
        this.blockUserId = blockUserId;
        this.createdDateTime = createdDateTime;
    }

    public static BlockResponse of(Block block) {
        return BlockResponse.builder()
                .blockId(block.getId())
                .comments(block.getComments())
                .userId(block.getUser().getId())
                .blockUserId(block.getBlockUser().getId())
                .createdDateTime(block.getCreatedDateTime())
                .build();
    }
}
