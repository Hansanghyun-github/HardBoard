package com.example.HardBoard.domain.emailNumber;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "email_numbers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailNumber {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_number_id")
    private Long id;

    private String email;

    @Column(name = "random_number")
    private String randomNumber;

    @Builder
    private EmailNumber(String email, String randomNumber) {
        this.email = email;
        this.randomNumber = randomNumber;
    }
}
