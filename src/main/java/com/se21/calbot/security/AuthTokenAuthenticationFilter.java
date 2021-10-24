package com.se21.calbot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;

/**
 * This class is used to initialize the token for Spring Security Context
 *
 */
@Component
public class AuthTokenAuthenticationFilter {
	@Autowired
	protected TokensRepository tokensRepository;
	@Autowired
	protected AuthenticationManager authenticationManager;

	/**
	 * 
	 * @param id the primary key of Token table
	 */
	public void doFilter(String id) {
		// get Token from DB
		AuthToken token = tokensRepository.findById(id).orElse(null);
		if (token != null) {
			// convert token to Spring Authentication using the adapter AuthTokenAuthentication
			AuthTokenAuthentication authTokenAuthentication = new AuthTokenAuthentication(token);
			try {
				// authenticate the token
				Authentication authentication = this.authenticationManager.authenticate(authTokenAuthentication);
				// set the token in the SecurityContextHolder so that other service can get its information
				// by using AuthenticationService
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (AuthenticationException e) {
				// if fail to authenticate, clean the SecurityContextHolder
				SecurityContextHolder.getContext().setAuthentication(null);
			}
		} else {
			// if token does not exist, clean the SecurityContextHolder
			SecurityContextHolder.getContext().setAuthentication(null);
		}

	}

}
