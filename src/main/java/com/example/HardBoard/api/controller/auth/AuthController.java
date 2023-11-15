package com.example.HardBoard.api.controller.auth;

import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.api.controller.auth.request.*;
import com.example.HardBoard.api.service.auth.AuthService;
import com.example.HardBoard.api.service.auth.MailService;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.api.service.token.TokenService;
import com.example.HardBoard.api.service.user.UserService;
import com.example.HardBoard.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/auth/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody AuthLoginRequest request
    ){
        authService.login(request.toServiceRequest());
        return ApiResponse.ok(tokenService.createTokens());
    }

    @PostMapping("/auth/join")
    public ApiResponse<String> join(
            @Valid @RequestBody AuthJoinRequest request
    ){
        if(log.isDebugEnabled()) log.debug("join start");

        userService.createUser(request.toUserServiceRequest());
        return ApiResponse.ok("ok");
    }

    @PostMapping("/auth/email/send")
    public ApiResponse<String> sendNumberToEmailCheckForJoin(
            @Valid @RequestBody AuthEmailSendRequest request
    ){
        if(userService.existsByEmail(request.getEmail()) == true) throw new IllegalArgumentException("이미 존재하는 이메일입니다.");

        mailService.sendEmail(request.toServiceRequest());
        return ApiResponse.ok("ok");
    }

    @PostMapping("/auth/users/email/send")
    public ApiResponse<String> sendNumberToEmailCheckForChangePassword(
            @Valid @RequestBody AuthEmailSendRequest request
    ){
        if(userService.existsByEmail(request.getEmail()) == false) throw new IllegalArgumentException("존재하지 않는 이메일입니다.");

        mailService.sendEmail(request.toServiceRequest());
        return ApiResponse.ok("ok");
    }

    @PostMapping("/auth/password/change")
    public ApiResponse<Object> checkNumberAndChangePasswordWithoutLogin(
            @Valid @RequestBody AuthChangePasswordRequest request
    ){
        mailService.isCorrectNumber(request.toMailCheckServiceRequest());
        authService.changePasswordWithoutAuthentication(request.toPasswordChangeServiceRequest());

        return ApiResponse.ok("ok");
    }

    @PostMapping("/users/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal PrincipalDetails principal){
        authService.logout(principal.getUser().getId());
        return ApiResponse.ok("ok");
    }

    @PostMapping("/users/refreshToken")
    public ApiResponse<TokenResponse> remadeRefreshToken(
            @Valid @RequestBody AuthRemadeTokenRequest request
    ){
        return ApiResponse.ok(tokenService.accessTokenExpired(
                request.getRefreshToken(), LocalDateTime.now()
        ));
    }
}
