package com.se21.calbot.security;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;

import lombok.extern.java.Log;

@Component
@Log
public class AuthTokenAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	protected TokensRepository tokensRepository;

	/**
	 * implement authenticate
	 */
	@Override
	public Authentication authenticate(Authentication authentication) {
		// check token's pk is not empty
		if (authentication.getCredentials() != null) {
			// although we can get token by calling getDetails(), 
			// we choose to query again for data consistency
			AuthToken authToken = this.tokensRepository.findById(authentication.getCredentials().toString()).orElse(null);
			if (authToken != null && authToken.getExpirydatetime().isAfter(LocalDateTime.now())) {
				AuthTokenAuthentication authenticatedAuthentication = new AuthTokenAuthentication(authToken);
				return authenticatedAuthentication;
			}
		}
		log.severe("token = "
				+ (authentication.getCredentials() == null ? "(NULL)" : authentication.getCredentials().toString()));
		throw new BadCredentialsException("BAD_TOKEN");
	}

	/**
	 * check if authentication can be used in AuthenticateProvider
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(AuthTokenAuthentication.class);
	}

}
