package com.example.HardBoard.api.service.user;

import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import com.example.HardBoard.api.service.user.response.UserResponse;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse createUser(
            UserCreateServiceRequest request
    ){
        return UserResponse.of(userRepository.save(request.toUser()));
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

    public void changePassword(
            Long userId,
            String prevPassword,
            String newPassword){
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid id"))
                .checkPassword(prevPassword)
                .changePassword(newPassword);

        // TODO 비밀번호 암호화를 언제 해야 할지
    }
}
