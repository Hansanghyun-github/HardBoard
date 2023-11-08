package com.example.HardBoard.domain.emailNumber;

import com.example.HardBoard.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailNumberRepository extends JpaRepository<EmailNumber, Long> {
    public Optional<EmailNumber> findByEmail(String email);
}
