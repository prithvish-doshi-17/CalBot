package com.se21.calbot.security;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.se21.calbot.model.AuthToken;

/**
 * This class is a adapter for AuthToken model to fit in Spring Security Authentication
 */
@SuppressWarnings("serial")
public class AuthTokenAuthentication implements Authentication {
	/**
	 * show if a user is authenticated
	 */
	protected boolean isAuthenticated;

	/**
	 * user's AuthToken saved in database
	 */
	protected AuthToken authToken;

	/**
	 * constructor
	 *
	 * @param authToken AuthToken object
	 */
	public AuthTokenAuthentication(AuthToken authToken) {
		this.authToken = authToken;
		if (this.authToken != null && this.authToken.getDiscordId() != null) {
			// if token exists, and its discord id(primary key) exists, consider this token is authenticated
			this.isAuthenticated = true;
		}
	}

	/**
	 * get Details
	 */
	@Override
	public Object getDetails() {
		return this.authToken;
	}

	/**
	 * get if the token is authenticated
	 */
	@Override
	public boolean isAuthenticated() {
		// check if token exists and it is not expired
		return this.isAuthenticated && authToken.getExpirydatetime().isAfter(LocalDateTime.now());
	}

	/**
	 * set if this Authentication is authenticated
	 */
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (!isAuthenticated) {
			this.isAuthenticated = false;
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@Override
	public String toString() {
		return "AuthTokenAuthentication [getCredentials()=" + getCredentials() + ", getDetails()=" + getDetails()
				+ ", getName()=" + getName() + ", getPrincipal()=" + getPrincipal() + "]";
	}

	/**
	 * get user name
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * get the role authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * get credentials
	 */
	@Override
	public Object getCredentials() {
		return this.authToken.getDiscordId();
	}

	/**
	 * get principal
	 */
	@Override
	public Object getPrincipal() {
		return this.authToken.getDiscordId();
	}

}
