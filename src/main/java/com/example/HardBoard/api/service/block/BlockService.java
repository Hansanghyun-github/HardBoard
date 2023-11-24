package com.example.HardBoard.api.service.block;

import com.example.HardBoard.api.service.block.request.BlockServiceRequest;
import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.block.BlockRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    public BlockResponse blockUser(BlockServiceRequest request) {
        User blockUser = userRepository.findById(request.getBlockUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid blockUserId"));
        Block block = blockRepository.save(
                Block.builder()
                        .comments(request.getComments())
                        .user(request.getUser())
                        .blockUser(blockUser)
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
}
