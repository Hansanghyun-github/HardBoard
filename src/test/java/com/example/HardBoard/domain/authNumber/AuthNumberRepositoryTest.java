package com.example.HardBoard.domain.authNumber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class AuthNumberRepositoryTest {
    @Autowired
    AuthNumberRepository authNumberRepository;
    @Test
    @DisplayName("이메일을 통해 객체를 찾는다")
    void findByEmail() throws Exception {
        // given
        String email="asfsdf@fds";
        AuthNumber authNumber = authNumberRepository.save(
                AuthNumber.builder()
                        .email(email)
                        .authNum(anyString())
                        .build());
        Long id = authNumber.getId();

        // when // then
        assertThat(authNumberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email")).getId())
                .isEqualTo(id);
    }

    @Test
    @DisplayName("이메일을 통해 객체가 존재하는지 알아낸다")
    void existsByEmail() throws Exception {
        // given
        String email = "email@email";
        authNumberRepository.save(
                AuthNumber.builder()
                        .email(email)
                        .authNum(anyString())
                        .build());

        // when // then
        assertThat(authNumberRepository.existsByEmail(email)).isTrue();
    }

    @Test
    @DisplayName("이메일이 존재하지 않으면 못 찾는다")
    void existsByEmailInFail() throws Exception {
        // given
        String email = "email@email";
        authNumberRepository.save(
                AuthNumber.builder()
                        .email(email)
                        .authNum(anyString())
                        .build());

        // when // then
        assertThat(authNumberRepository.existsByEmail(email+"fsad")).isFalse();
    }
}