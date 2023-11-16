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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthValidationService authValidationService;
    
    // TODO 액세스토큰에 어떤 정보를 넣어야 할까. email만 넣으면 충분할까? 커넥션을 안보내고 토큰에서 정보를 추출하는 것이 좋을까?

    public TokenResponse createTokens(Long currentTimeMillis){
        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        User user = principal.getUser();


        String accessToken = JWT.create()
                .withSubject(user.getNickname())
                .withExpiresAt(new Date(
                        currentTimeMillis + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.create(user, LocalDateTime.now()));

        return new TokenResponse(JwtProperties.TOKEN_PREFIX + accessToken, refreshToken.getRefreshToken());
    }

    public TokenResponse accessTokenExpired(String refreshToken, Long currentTimeMillis){
        // verify RefreshToken
        authValidationService.verifyRefreshToken(refreshToken);

        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        token.refreshTokenRotation(
                Instant.ofEpochMilli(currentTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        ); // JWT access token이 Date를 써서 아쩔 수 없이 식이 길어졌다.

        User user = token.getUser(); // TODO LAZY라 추가 커넥션 발생 할 듯
        String accessToken = JWT.create()
                .withSubject(user.getNickname())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return new TokenResponse(JwtProperties.TOKEN_PREFIX + accessToken, token.getRefreshToken());
    }
}
