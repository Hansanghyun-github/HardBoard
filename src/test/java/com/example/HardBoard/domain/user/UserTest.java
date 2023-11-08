package com.example.HardBoard.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class UserTest {
    @Test
    @DisplayName("유저의 닉네임을 변경한다")
    void changeNickname() throws Exception {
        // given
        String nickname = "nickname1";
        User user = User.builder()
                .email(anyString())
                .password(anyString())
                .nickname(nickname)
                .build();

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
        User user = User.builder()
                .email(anyString())
                .password(password)
                .nickname(anyString())
                .build();

        String newPassword = "newPassword";


        // when
        user.changePassword(newPassword);

        // then
        assertThat(user.getPassword()).isNotEqualTo(password);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }
    
    @Test
    @DisplayName("같은 비밀번호를 입력하면 성공한다")
    void checkSamePassword() throws Exception {
        // given
        String password = "password1";
        User user = User.builder()
                .email(anyString())
                .password(password)
                .nickname(anyString())
                .build();

        
        // when // then
        assertThat(user.checkPassword(password)).isEqualTo(user);
    }

    @Test
    @DisplayName("다른 비밀번호를 입력하면 실패한다")
    void checkDifferentPassword() throws Exception {
        // given
        String password = "password1";
        User user = User.builder()
                .email(anyString())
                .password(password)
                .nickname(anyString())
                .build();

        // when // then
        assertThatThrownBy(() -> user.checkPassword(password+"!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");
    }
}