package com.example.HardBoard.domain.authNumber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AuthNumberTest {

    @Test
    @DisplayName("인증번호를 변경한다")
    void changeAuthNum() throws Exception {
        // given
        String authNum = "number";
        AuthNumber authNumber = AuthNumber.builder()
                .email("gks@gks")
                .authNum(authNum)
                .build();

        // when
        authNumber.changeAuthNum(authNum+"gks");

        // then
        assertThat(authNumber.getAuthNum()).isEqualTo(authNum+"gks");
    }

    @Test
    @DisplayName("올바른 인증번호입니다")
    void isCorrectAuthNum() throws Exception {
        // given
        String authNum = "number";
        AuthNumber authNumber = AuthNumber.builder()
                .email("gks@gks")
                .authNum(authNum)
                .build();

        // when // then
        assertThat(authNumber.isCorrectAuthNum(authNum)).isTrue();
    }

    @Test
    @DisplayName("올바른 인증번호를 입력하지 않으면 실패한다")
    void wrongAuthNumInFail() throws Exception {
        // given
        String authNum = "number";
        AuthNumber authNumber = AuthNumber.builder()
                .email("gks@gks")
                .authNum(authNum)
                .build();

        // when // then
        assertThatThrownBy(() -> authNumber.isCorrectAuthNum(authNum + "!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 인증번호입니다");
    }
}