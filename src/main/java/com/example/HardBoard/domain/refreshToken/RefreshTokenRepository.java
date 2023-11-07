package com.example.HardBoard.domain.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public Optional<RefreshToken> findByRefreshToken(String refreshToken);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);
}
