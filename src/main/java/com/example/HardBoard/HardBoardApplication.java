package com.example.HardBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HardBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(HardBoardApplication.class, args);
	}

}
