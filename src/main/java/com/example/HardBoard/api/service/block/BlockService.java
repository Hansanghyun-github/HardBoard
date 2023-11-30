package com.example.HardBoard.api.service.block;

import com.example.HardBoard.api.service.block.request.BlockServiceRequest;
import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.block.BlockRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    public BlockResponse blockUser(BlockServiceRequest request, LocalDateTime createdDateTime) {
        User blockUser = userRepository.findById(request.getBlockUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid blockUserId"));
        Block block = blockRepository.save(
                Block.builder()
                        .comments(request.getComments())
                        .user(request.getUser())
                        .blockUser(blockUser)
                        .createdDateTime(createdDateTime)
                        .build()
        );
        return BlockResponse.of(block);
    }

    public void cancelBlockUser(User user, Long blockUserId) {
        User blockUser = userRepository.findById(blockUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid blockUserId"));
        blockRepository.deleteByUserAndBlockUser(user, blockUser);
        // TODO delete 할때 해당 객체가 없다면, 내가 원하는 Exception을 지정해 줄 수 있을까?
    }

    public List<BlockResponse> getBlockList(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 20,
                Sort.by(Sort.Direction.DESC, "createdDateTime"));
        return blockRepository.findByUserId(userId, pageRequest)
                .map(BlockResponse::of)
                .getContent();
    }
}
