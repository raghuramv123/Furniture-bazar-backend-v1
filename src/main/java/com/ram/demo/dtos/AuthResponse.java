package com.ram.demo.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

	private String accessToken;
	private String refreshToken;
	private String tokenType="Bearer";
	private Long expiresIn;//seconds
	private String role;
	private String email;
}
