package com.example.HardBoard.config.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtProperties {
	public static String SECRET = "FDEWF123DWEFS32R3409FDSKLSG324DSF";
	public static Long ACCESS_TOKEN_EXPIRATION_TIME = 10*60*1000L;
	public static String TOKEN_PREFIX = "Bearer ";
	public static String HEADER_STRING = "Authorization";
	public static Long REFRESH_TOKEN_EXPIRATION_TIME = 60*60*24*14L;
}
