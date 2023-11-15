package com.example.HardBoard.domain.authNumber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthNumberRepository extends JpaRepository<AuthNumber, Long> {
    public Optional<AuthNumber> findByEmail(String email);

    public boolean existsByEmail(String email);
}
