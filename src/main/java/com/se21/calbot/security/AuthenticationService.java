package com.se21.calbot.security;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;

/**
 * Provide all information of user
 */
@Component
public class AuthenticationService {
	@Autowired
	TokensRepository tokensRepository;

	/**
	 * @return token id
	 */
	public String getId() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getDiscordId();
		}
		return null;
	}

	/**
	 * @return get the token saved in SecurityContextHolder
	 */
	public String getToken() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getToken();
		}
		return null;
	}

	/**
	 * @return get code
	 */
	public String getCode() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getCode();
		}
		return null;
	}

	/**
	 * @return get expired date time
	 */
	public LocalDateTime getExpirydatetime() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getExpirydatetime();
		}
		return null;
	}

	/**
	 * @return get refresh token
	 */
	public String getRefreshToken() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getRefreshToken();
		}
		return null;
	}

	/**
	 * @return get scope
	 */
	public String getScope() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getScope();
		}
		return null;
	}

	/**
	 * @return get calType
	 */
	public String getCalType() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getCalType();
		}
		return null;
	}

	/**
	 * @return get calId
	 */
	public String getCalId() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			AuthToken token = (AuthToken) SecurityContextHolder.getContext().getAuthentication().getDetails();
			return token.getCalId();
		}
		return null;
	}
	
	/**
	 * @return return if token is authenticated
	 */
	public boolean isAuthenticated() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
		}
		return false;
	}

	/**
	 * @return get token from database
	 */
	public AuthToken getAuthToken() {
		return tokensRepository.findById(this.getId()).orElse(null);
	}
}
