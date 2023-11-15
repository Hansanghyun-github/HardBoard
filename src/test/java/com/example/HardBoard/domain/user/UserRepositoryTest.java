package com.example.HardBoard.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired UserRepository userRepository;
    @Test
    @DisplayName("이메일을 통해 유저를 찾는다")
    void findByEmail() throws Exception {
        // given
        String email = "gks5828@dk";

        User user = User.builder()
                .email(email)
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // when
        Optional<User> byEmail = userRepository.findByEmail(email);

        // then
        assertThat(byEmail.isPresent()).isTrue();
        assertThat(byEmail.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 유저가 존재하는지 확인한다(True)")
    void existsUserByEmail() throws Exception {
        // given
        String email = "gks5828@dk";

        User user = User.builder()
                .email(email)
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // when // then
        assertThat(userRepository.existsByEmail(email)).isEqualTo(true);
    }

    @Test
    @DisplayName("이메일로 유저가 존재하는지 확인한다(False)")
    void existsUserByEmailInFail() throws Exception {
        // given
        String email = "gks5828@dk";

        User user = User.builder()
                .email(email)
                .password("sdfsef")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // when // then
        assertThat(userRepository.existsByEmail(email+"fds")).isEqualTo(false);
    }
}