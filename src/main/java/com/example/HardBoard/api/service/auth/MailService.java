package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.MailCheckServiceRequest;
import com.example.HardBoard.api.service.auth.request.MailSendServiceRequest;
import com.example.HardBoard.domain.authNumber.AuthNumber;
import com.example.HardBoard.domain.authNumber.AuthNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final AuthNumberRepository authNumberRepository;

    private final Random random = ThreadLocalRandom.current();

    public void sendEmail(MailSendServiceRequest request){

        String authNum = makeAuthNumber();
        authNumberRepository.save(AuthNumber.builder()
                .email(request.getTo())
                .authNum(authNum)
                .build());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo());
        message.setSubject("HardBoard 인증번호입니다.");
        message.setText("HardBoard 이용을 위한 이메일 인증번호는\n" + authNum + " 입니다.");

        mailSender.send(message);
    }

    public boolean isCorrectNumber(MailCheckServiceRequest request){
        return authNumberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 이메일입니다"))
                .isCorrectAuthNum(request.getAuthNumber());
    }

    public String makeAuthNumber(){
        return "" + (random.nextLong(888888) + 111111L);
    }
}
