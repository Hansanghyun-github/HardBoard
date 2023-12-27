package com.example.HardBoard.domain.authNumber;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "auth_numbers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthNumber {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_number_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "auth_num", nullable = false)
    private String authNum;

    @Builder
    private AuthNumber(String email, String authNum) {
        this.email = email;
        this.authNum = authNum;
    }

    public void changeAuthNum(String authNum){
        this.authNum = authNum;
    }

    public boolean isCorrectAuthNum(String authNum){
        if(!this.authNum.equals(authNum))
            throw new IllegalArgumentException("올바르지 않은 인증번호입니다");
        return true;
    }
}
