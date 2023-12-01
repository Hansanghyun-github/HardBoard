package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthValidationService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void verifyPathUserId(Long userId){
        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if(!userId.equals(principal.getUser().getId()))
            throw new IllegalArgumentException("Invalid userId");
    }

    @Transactional
    public void verifyRefreshToken(String refreshToken){
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if(token.isExpired(LocalDateTime.now()))
            throw new IllegalArgumentException("RefreshToken was expired");
    }
}
