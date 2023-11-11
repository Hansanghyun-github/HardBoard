package com.example.HardBoard.api.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.service.auth.AuthValidationService;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthValidationService authValidationService;

    public TokenResponse createTokens(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated())
            throw new IllegalStateException("로그인하지 않았습니다");

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = JWT.create()
                .withSubject(user.getNickname())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.create(user, LocalDateTime.now()));

        return new TokenResponse(JwtProperties.TOKEN_PREFIX + accessToken, refreshToken.getRefreshToken());
    }

    public TokenResponse accessTokenExpired(String refreshToken, LocalDateTime dateTime){
        // verifyRefreshToken
        authValidationService.verifyRefreshToken(refreshToken);

        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        token.refreshTokenRotation(dateTime);

        User user = token.getUser();
        String accessToken = JWT.create()
                .withSubject(user.getNickname())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return new TokenResponse(JwtProperties.TOKEN_PREFIX + accessToken, token.getRefreshToken());
    }
}
