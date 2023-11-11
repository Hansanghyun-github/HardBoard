package com.example.HardBoard.api.controller.user;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.user.request.UserChangeNicknameRequest;
import com.example.HardBoard.api.controller.user.request.UserChangePasswordRequest;
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

    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> findUser(
            @PathVariable Long userId
    ){
        //BeforeRequest.verifyUser(userId);
        return null;
    }

    @PutMapping("/users/{userId}/nickname")
    public ApiResponse<String> changeNickname(
            @PathVariable Long userId,
            @Valid @RequestBody UserChangeNicknameRequest request
    ){
        //BeforeRequest.verifyUser(userId);
        return null;
    }

    @PutMapping("/users/{userId}/password")
    public ApiResponse<String> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserChangePasswordRequest request
            ){
        log.info("enter change password");
        //BeforeRequest.verifyUser(userId);
        return ApiResponse.ok("ok");
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<String> deleteUser(
            @PathVariable Long userId
    ){
        //BeforeRequest.verifyUser(userId);
        return null;
    }

}
