package com.example.HardBoard.api.service.auth;

import com.example.HardBoard.api.service.auth.request.AuthLoginServiceRequest;
import com.example.HardBoard.api.service.auth.request.AuthPasswordChangeServiceRequest;
import com.example.HardBoard.config.auth.PrincipalDetails;
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
        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long userId = principal.getUser().getId();

        if(!refreshTokenRepository.existsByUserId(userId))
            throw new IllegalArgumentException("Invalid id");
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void changePasswordWithoutAuthentication(AuthPasswordChangeServiceRequest request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"))
                .checkPassword(passwordEncoder, request.getPrevPassword())
                .changePassword(passwordEncoder, request.getNewPassword());
    }
}
