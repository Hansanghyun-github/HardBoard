package com.example.HardBoard.api.service.user;

import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import com.example.HardBoard.api.service.user.request.UserPasswordChangeServiceRequest;
import com.example.HardBoard.api.service.user.response.UserResponse;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserConverter;
import com.example.HardBoard.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserConverter userConverter = new UserConverter(passwordEncoder);
    @Test
    @DisplayName("유저를 생성한다")
    void createUser() throws Exception {
        // given
        String email = "email@fasd";
        String password = "passwordd";
        String nickname = "nnn";
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(email)
                        .password(password)
                        .nickname(nickname)
                        .build();
        
        // when
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);

    }
    @Test
    @DisplayName("유저id를 통해 유저 정보를 조회한다")
    void findUserById() throws Exception {
        // given
        String email = "email@fasd";
        String password = "passwordd";
        String nickname = "nnn";
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(email)
                        .password(password)
                        .nickname(nickname)
                        .build();
        User user1 = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user1.getId();


        // when
        UserResponse user = userService.findUserById(userId);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("잘못된 유저id로 조회하면 에러가 난다")
    void findUserDifferentIdBeError() throws Exception {
        // given
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(anyString())
                        .password(anyString())
                        .nickname(anyString())
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();


        // when // then
        assertThatThrownBy(() -> userService.findUserById(userId + 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid id");
    }
    
    @Test
    @DisplayName("유저를 삭제한다")
    void deleteUser() throws Exception {
        // given
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(anyString())
                        .password(anyString())
                        .nickname(anyString())
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();
        
        // when
        userService.deleteUser(userId);
        
        // then
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void changeNickname() throws Exception {
        // given
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(anyString())
                        .password(anyString())
                        .nickname(anyString())
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();

        String newNickname = "newNickname";

        // when
        userService.changeNickname(userId, newNickname);

        // then
        assertThat(userService.findUserById(userId).getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() throws Exception {
        // given
        String prevPassword = "password";
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(anyString())
                        .password(prevPassword)
                        .nickname(anyString())
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();

        String newPassword = "newPassword";

        // when
        userService.changePassword(UserPasswordChangeServiceRequest
                .builder()
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
        UserCreateServiceRequest request =
                UserCreateServiceRequest.builder()
                        .email(anyString())
                        .password(prevPassword)
                        .nickname(anyString())
                        .build();
        User user = userRepository.save(userConverter.toEntity(request.toDomainRequest()));
        Long userId = user.getId();

        String newPassword = "newPassword";

        // when // then
        assertThatThrownBy(() ->
                userService.changePassword(UserPasswordChangeServiceRequest
                        .builder()
                        .prevPassword(prevPassword + "fdsf")
                        .newPassword(newPassword)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");
    }
}