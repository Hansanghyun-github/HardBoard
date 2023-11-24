package com.example.HardBoard.api.controller.block;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.block.request.BlockRequest;
import com.example.HardBoard.api.service.block.BlockService;
import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/blocks/{blockUserId}")
    public ApiResponse<BlockResponse> blockUser(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long blockUserId,
            @Valid @RequestBody BlockRequest request
            ){
        return ApiResponse.ok(blockService.blockUser(request.toServiceRequest(principal.getUser(), blockUserId)));
    }

    @DeleteMapping("/blocks/{blockUserId}")
    public ApiResponse<String> cancelBlockUser(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long blockUserId
    ){
        blockService.cancelBlockUser(principal.getUser(), blockUserId);
        return ApiResponse.ok("ok");
    }

    @GetMapping("/blocks") // TODO 페이징 처리 - 나중에 (테스트도)
    public ApiResponse<List<BlockResponse>> getBlockList(
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        return null;
    }
}
