package com.example.HardBoard.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.HardBoard.api.ApiResponse;
import com.example.HardBoard.config.auth.PrincipalDetails;
import com.example.HardBoard.config.auth.JwtProperties;
import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO 여기서 exception 터졌을때 처리해주는 filter 생성
		// TODO Test

		String header = request.getHeader(JwtProperties.HEADER_STRING);
		if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			try{
				chain.doFilter(request, response);
			} catch (AccessDeniedException e){
				log.debug("access failed handler receive access denied exception");
				ObjectMapper mapper = new ObjectMapper();
				String result = mapper.writeValueAsString(ApiResponse.of(HttpStatus.FORBIDDEN, "Authorization failed"));
				response.getWriter().write(result);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			} catch (Exception e){
				log.debug(e.getMessage());

			}

			return;
		}
		String token = request.getHeader(JwtProperties.HEADER_STRING)
				.replace(JwtProperties.TOKEN_PREFIX, "");

		// 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
		// 내가 SecurityContext에 집적접근해서 세션을 만들때 자동으로 UserDetailsService에 있는
		// loadByUsername이 호출됨.
		String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
				.getClaim("username").asString();
		if (StringUtils.hasText(username) == false) {
			log.debug("access denied");
			throw new AccessDeniedException("유저를 찾을수없습니다");
		}

		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new AccessDeniedException("유저를 찾을 수 없습니다"));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		PrincipalDetails principalDetails = new PrincipalDetails(user);
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
				principalDetails,
				null,
				principalDetails.getAuthorities());
		context.setAuthentication(authentication);

		// 강제로 시큐리티의 세션에 접근하여 값 저장
		SecurityContextHolder.setContext(context);

		chain.doFilter(request, response);

	}

}
