package com.example.HardBoard.api.controller.user;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.user.request.UserChangeNicknameRequest;
import com.example.HardBoard.api.controller.user.request.UserChangePasswordRequest;
import com.example.HardBoard.api.service.auth.AuthValidationService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.api.service.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthValidationService authValidationService;

    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> findUser(
            @PathVariable Long userId
    ){
        authValidationService.verifyPathUserId(userId);
        return ApiResponse.ok(userService.findUserById(userId));
    }

    @PatchMapping("/users/{userId}/nickname")
    public ApiResponse<String> changeNickname(
            @PathVariable Long userId,
            @Valid @RequestBody UserChangeNicknameRequest request
    ){
        authValidationService.verifyPathUserId(userId);
        userService.changeNickname(userId, request.getNewNickname());
        return ApiResponse.ok("ok");
    }

    @PatchMapping("/users/{userId}/password")
    public ApiResponse<String> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserChangePasswordRequest request
            ){
        authValidationService.verifyPathUserId(userId);
        userService.changePassword(userId, request.toServiceRequest());
        return ApiResponse.ok("ok");
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<String> deleteUser(
            @PathVariable Long userId
    ){
        authValidationService.verifyPathUserId(userId);
        userService.deleteUser(userId);
        return ApiResponse.ok("ok");
    }

}
