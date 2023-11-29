package com.example.HardBoard.domain.user;

import com.example.HardBoard.domain.user.request.UserCreateDomainRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class UserTest {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    UserConverter userConverter = new UserConverter(passwordEncoder);
    @Test
    @DisplayName("유저의 닉네임을 변경한다")
    void changeNickname() throws Exception {
        // given
        String nickname = "nickname1";

        User user = userConverter.toEntity(UserCreateDomainRequest.builder()
                .email("email@email")
                .password(passwordEncoder.encode("password"))
                .nickname(nickname)
                .build());

        String newNickname = "newNickname";

        // when
        user.changeNickname(newNickname);

        // then
        assertThat(user.getNickname()).isNotEqualTo(nickname);
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void changePassword() throws Exception {
        // given
        String password = "password1";
        User user = userConverter.toEntity(UserCreateDomainRequest.builder()
                .email("email@email")
                .password(password)
                .nickname("nickname")
                .build());

        String newPassword = "newPassword";


        // when
        user.changePassword(passwordEncoder, newPassword);

        // then
        assertThat(passwordEncoder.matches(password, user.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();
    }
    
    @Test
    @DisplayName("같은 비밀번호를 입력하면 성공한다")
    void checkSamePassword() throws Exception {
        // given
        String password = "password1";
        User user = userConverter.toEntity(UserCreateDomainRequest.builder()
                .email("email@email")
                .password(password)
                .nickname("nickname")
                .build());

        
        // when // then
        assertThat(user.checkPassword(passwordEncoder, password)).isEqualTo(user);
    }

    @Test
    @DisplayName("다른 비밀번호를 입력하면 실패한다")
    void checkDifferentPassword() throws Exception {
        // given
        String password = "password1";
        User user = userConverter.toEntity(UserCreateDomainRequest.builder()
                .email("email@email")
                .password(password)
                .nickname("nickname")
                .build());

        // when // then
        assertThatThrownBy(() -> user.checkPassword(passwordEncoder, password+"!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");
    }
}