package com.example.HardBoard.domain.user;

import com.example.HardBoard.domain.block.Block;
import com.example.HardBoard.domain.block.BlockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired UserRepository userRepository;
    @Autowired
    BlockRepository blockRepository;

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

    @Test
    @DisplayName("이메일 중복 체크")
    void checkEmailDuplicate() throws Exception {
        // given

        String email = "email@email";
        String nickname1 = "nickname1";
        String nickname2 = "nickname2";
        User user1 = User.builder()
                .email(email)
                .password("password")
                .nickname(nickname1)
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .email(email)
                .password("password")
                .nickname(nickname1)
                .role(Role.ROLE_USER)
                .build();

        // when // then
        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("닉네임 중복 체크")
    void checkNicknameDuplicate() throws Exception {
        // given

        String email1 = "email1@email";
        String email2 = "email2@email";
        String nickname = "nickname";
        User user1 = User.builder()
                .email(email1)
                .password("password")
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .email(email2)
                .password("password")
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build();

        // when // then
        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}