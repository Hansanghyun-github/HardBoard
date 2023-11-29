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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/blocks/{blockUserId}")
    public ApiResponse<BlockResponse> blockUser( // TODO 유저 차단 최대 크기 제한 (100명?)
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long blockUserId,
            @Valid @RequestBody BlockRequest request
            ){
        return ApiResponse.ok(blockService.blockUser(request.toServiceRequest(principal.getUser(), blockUserId), LocalDateTime.now()));
    }

    @DeleteMapping("/blocks/{blockUserId}")
    public ApiResponse<String> cancelBlockUser(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long blockUserId
    ){
        blockService.cancelBlockUser(principal.getUser(), blockUserId);
        return ApiResponse.ok("ok");
    }

    @GetMapping("/blocks")
    public ApiResponse<List<BlockResponse>> getBlockList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal PrincipalDetails principal
    ){
        if(page <= 0) throw new IllegalArgumentException("page has to be greater than zero");
        return ApiResponse.ok(blockService.getBlockList(principal.getUser().getId(), page));
    }
}
