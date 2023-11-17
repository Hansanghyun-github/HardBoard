package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import com.example.HardBoard.api.service.auth.request.AuthPasswordChangeServiceRequest;
import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
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
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(loginEmail)
                .password(loginPassword)
                .nickname(nickname)
                .build()));
        AuthLoginServiceRequest request = new AuthLoginServiceRequest(email, password);


        // when // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("자격 증명에 실패하였습니다.");
    }

    @Test
    @DisplayName("로그아웃 한다")
    void logout() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build()));
        AuthLoginServiceRequest request = new AuthLoginServiceRequest(email, password);
        authService.login(request);

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(LocalDateTime.now().plusSeconds(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .build());

        Long userId = user.getId();

        // when // then
        authService.logout(userId);
    }

    @Test
    @DisplayName("리프레시토큰이 없으면 로그아웃 실패한다")
    void logoutWithoutRefreshTokenInFail() throws Exception {
        // given
        String email = "email@email";
        String password = "password";
        String nickname = "nickname";
        User user = userRepository.save(userConverter.toEntity(UserCreateDomainRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build()));
        AuthLoginServiceRequest request = new AuthLoginServiceRequest(email, password);
        authService.login(request);

        Long userId = user.getId();

        // when // then
        assertThatThrownBy(() -> authService.logout(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid id");
    }

    @Test
    @DisplayName("인증 없이 비밀번호를 변경한다")
    void changePassword() throws Exception {
        // given
        String prevPassword = "password";
        String email = "gks@gks";
        String nickname = "nickname";
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(email)
                        .password(prevPassword)
                        .nickname(nickname)
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();

        String newPassword = "newPassword";

        // when
        authService.changePasswordWithoutAuthentication(AuthPasswordChangeServiceRequest
                .builder()
                .email(request.getEmail())
                .prevPassword(prevPassword)
                .newPassword(newPassword)
                .build());
        String userPassword = user.getPassword();

        // then
        assertThat(passwordEncoder.matches(prevPassword, userPassword)).isFalse();
        assertThat(passwordEncoder.matches(newPassword, userPassword)).isTrue();
    }

    @Test
    @DisplayName("비밀번호를 변경할 때 잘못된 비밀번호를 이용하면 에러가 난다")
    void changePasswordUsingWrongPasswordBeError() throws Exception {
        // given
        String prevPassword = "password";
        String email = "gks@gks";
        String nickname = "nickname";
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(email)
                        .password(prevPassword)
                        .nickname(nickname)
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();

        String newPassword = "newPassword";

        // when // then
        assertThatThrownBy(() ->
                authService.changePasswordWithoutAuthentication(AuthPasswordChangeServiceRequest
                        .builder()
                        .email(request.getEmail())
                        .prevPassword(prevPassword + "fdsf")
                        .newPassword(newPassword)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");
    }

}