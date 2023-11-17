package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import com.example.HardBoard.api.service.auth.request.MailSendServiceRequest;
import com.example.HardBoard.domain.authNumber.AuthNumber;
import com.example.HardBoard.domain.authNumber.AuthNumberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MailServiceTest {
    @Autowired AuthNumberRepository authNumberRepository;
    @Autowired MailService mailService;

    @MockBean JavaMailSender javaMailSender;

    @Test
    @DisplayName("이메일에 인증번호를 보낸다")
    void sendMailAuthNumber() throws Exception {
        // given
        MailSendServiceRequest request = MailSendServiceRequest.builder()
                .to("han41562@gmail.com")
                .build();

        // when // then
        mailService.sendEmail(request);

        verify(javaMailSender, times(1)).send(ArgumentMatchers.any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일과 인증번호를 통해 인증한다")
    void authenticateUsingEmailAndNumber() throws Exception {
        // given
        String email = "gsdg@gsd";
        String authNum = mailService.makeAuthNumber();
        AuthNumber authNumber = AuthNumber.builder()
                .email(email)
                .authNum(authNum)
                .build();
        authNumberRepository.save(authNumber);

        MailCheckServiceRequest request = MailCheckServiceRequest.builder()
                .email(email)
                .authNumber(authNum)
                .build();

        // when // then
        mailService.isCorrectNumber(request);

    }

    @ParameterizedTest
    @CsvSource(value = {"email@gks, randomNumber, email@gks, wrongNumber, 올바르지 않은 인증번호입니다",
            "email@gks, randomNumber, wrong@email, randomNumber, 올바르지 않은 이메일입니다"})
    @DisplayName("잘못된 이메일이나 인증번호를 이용하면 인증 실패한다")
    void usingWrongEmailOrNumberBeFailed(
            String email,
            String randomNumber,
            String authEmail,
            String authNum,
            String message
            ) throws Exception {
        // given
        AuthNumber authNumber = AuthNumber.builder()
                .email(email)
                .authNum(randomNumber)
                .build();
        authNumberRepository.save(authNumber);

        MailCheckServiceRequest request = MailCheckServiceRequest.builder()
                .email(authEmail)
                .authNumber(authNum)
                .build();

        // when // then
        assertThatThrownBy(() -> mailService.isCorrectNumber(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }
}