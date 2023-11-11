package com.example.HardBoard.api.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import com.example.HardBoard.api.service.auth.response.TokenResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.domain.refreshToken.RefreshToken;
import com.example.HardBoard.domain.refreshToken.RefreshTokenRepository;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthValidationService authValidationService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void login(AuthLoginServiceRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.isAuthenticated())
            throw new IllegalStateException("로그인하지 않았습니다");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUser().getId();

        if(!refreshTokenRepository.existsByUserId(userId))
            throw new IllegalArgumentException("Invalid id");
        refreshTokenRepository.deleteByUserId(userId);
    }

    // TODO 인증없이 비밀번호 변경하는 메서드 추가
}
