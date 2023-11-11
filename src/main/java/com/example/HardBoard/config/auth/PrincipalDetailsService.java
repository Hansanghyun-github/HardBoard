package com.example.HardBoard.config.auth;

import com.example.HardBoard.domain.user.User;
import com.example.HardBoard.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(username);

		if(user.isEmpty()){
			log.debug("don't found user using username");
			throw new UsernameNotFoundException("username을 찾을 수 없습니다");
		}
		log.debug("found user");

		return new PrincipalDetails(user.get());
	}
}
