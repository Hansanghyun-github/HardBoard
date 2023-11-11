package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.Role;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired UserRepository userRepository;
    @Autowired AuthService authService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UserConverter userConverter = new UserConverter(passwordEncoder);

    @Test
    @DisplayName("로그인에 성공한다")
    void loginSuccess() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(email)
                .password(password)
                .nickname(anyString())
                .build()));
        AuthLoginServiceRequest request = new AuthLoginServiceRequest(email, password);

        // when // then
        authService.login(request);

        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        User loginUser = principal.getUser();

        assertThat(loginUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(loginUser.getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches(password, loginUser.getPassword()));
    }

    @ParameterizedTest
    @CsvSource(value = {"email@email,", ",password", "emai@em,password", "email@email,pass"})
    @DisplayName("올바르지 않은 이메일이나 비밀번호를 입력하면 로그인에 실패한다")
    void loginWithWrongEmailOrPasswordInFail(String email, String password) throws Exception {
        // given
        String loginEmail = "email@email";
        String loginPassword = "password";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(loginEmail)
                .password(loginPassword)
                .nickname(anyString())
                .build()));
        AuthLoginServiceRequest request = new AuthLoginServiceRequest(email, password);


        // when // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @Test
    @DisplayName("액세스토큰과 리프레시토큰을 생성한다")
    void createAccessTokenAndRefreshToken() throws Exception {
        // given


        // when

        // then
    }

    @Test
    @DisplayName("토큰들을 만들 때 유효하지 않은 유저id를 입력하면 실패한다")
    void failToCreateAccessTokenAndRefreshTokenByWrongUserId() throws Exception {
        // given


        // when

        // then
    }

    @Test
    @DisplayName("리프레시토큰 다시 만든다")
    void remoadeRefreshToken() throws Exception {
        // given

        // when
        
        // then
    }
    
    @Test
    @DisplayName("리프레시토큰을 만들 때 잘못된 유저id 또는 이전 리프레시토큰를 입력하면 실패한다")
    void failToRemadeRefreshTokenByWrongUserIdOrPreviousRefreshToken() throws Exception {
        // given

        
        // when
        
        // then
    }

}