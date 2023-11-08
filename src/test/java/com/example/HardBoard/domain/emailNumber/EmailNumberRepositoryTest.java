package com.example.HardBoard.domain.emailNumber;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@Transactional
class EmailNumberRepositoryTest {
    @Autowired EmailNumberRepository emailNumberRepository;
    @Test
    @DisplayName("이메일을 통해 객체를 찾는다")
    void findByEmail() throws Exception {
        // given
        String email="asfsdf@fds";
        EmailNumber emailNumber = emailNumberRepository.save(
                EmailNumber.builder()
                        .email(email)
                        .randomNumber(anyString())
                        .build());
        Long id = emailNumber.getId();

        // when // then
        assertThat(emailNumberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email")).getId())
                .isEqualTo(id);
    }
}