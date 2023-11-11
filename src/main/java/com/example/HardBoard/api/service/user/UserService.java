package com.example.HardBoard.api.service.user;

import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import com.example.HardBoard.api.service.user.request.UserPasswordChangeServiceRequest;
import com.example.HardBoard.api.service.user.response.UserResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(
            UserCreateServiceRequest request
    ){
        return UserResponse.of(userRepository.save(userConverter.toEntity(request.toDomainRequest())));
    }

    public UserResponse findUserById(Long userId){
        return UserResponse.of(userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid id")));
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public void changeNickname(Long userId, String nickname){
        userRepository.findById(userId)
                .orElseThrow(() ->
                new IllegalArgumentException("Invalid id"))
                .changeNickname(nickname);
        // TODO 글자 수 제한
    }

    public void changePassword(UserPasswordChangeServiceRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.isAuthenticated())
            throw new IllegalStateException("로그인하지 않았습니다");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        userRepository.findById(principal.getUser().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid id"))
                .checkPassword(passwordEncoder, request.getPrevPassword())
                .changePassword(passwordEncoder, request.getNewPassword());

        // TODO 비밀번호 암호화를 언제 해야 할지
    }
}
