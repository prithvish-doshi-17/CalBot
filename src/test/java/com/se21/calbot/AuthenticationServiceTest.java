package com.se21.calbot;


import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.security.AuthTokenAuthenticationFilter;
import com.se21.calbot.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The tests for {@link com.se21.calbot.security.AuthenticationService AuthenticationService.java}
 */
@SpringBootTest(classes = SpringUnitTestConfig.class)
public class AuthenticationServiceTest {

    @Autowired
    AuthTokenAuthenticationFilter authTokenAuthenticationFilter;

    @Autowired
    AuthenticationService service;

    @Autowired
    TokensRepository tokensRepository;

    private final LocalDateTime now = LocalDateTime.now().plusDays(1);
    private AuthToken testToken;

    @BeforeEach
    public void init() {
         this.testToken = new AuthToken("authTest", "token", "code",
                now, "refreshToken", "scope", "calType", "calId");
        tokensRepository.save(testToken);

    }

    /**
     * When login with exist token, AuthenticationService should return correct data
     */
    @Test
    public void serviceShouldReturnCorrectDataWhenTokenExist(){
        authTokenAuthenticationFilter.doFilter("authTest");
        assertEquals(service.getId(), testToken.getDiscordId());
        assertEquals(service.getToken(), testToken.getToken());
        assertEquals(service.getCode(), testToken.getCode());
        assertEquals(service.getExpirydatetime(), testToken.getExpirydatetime());
        assertEquals(service.getRefreshToken(), testToken.getRefreshToken());
        assertEquals(service.getScope(), testToken.getScope());
        assertEquals(service.getCalId(), testToken.getCalId());
        assertEquals(service.getCalType(), testToken.getCalType());
        assertTrue(service.getAuthToken() != null);
        assertEquals(service.getAuthToken().getDiscordId(), testToken.getDiscordId());
        assertTrue(service.isAuthenticated());
    }

    /**
     * When login with non-exist token, AuthenticationService would not provide any user information
     */
    @Test
    public void serviceShouldReturnNothing(){
        authTokenAuthenticationFilter.doFilter("nonExist");
        assertEquals(service.getId(), null);
        assertEquals(service.getToken(), null);
        assertEquals(service.getCode(), null);
        assertEquals(service.getExpirydatetime(), null);
        assertEquals(service.getRefreshToken(), null);
        assertEquals(service.getScope(), null);
        assertEquals(service.getCalId(), null);
        assertEquals(service.getCalType(), null);
        assertTrue(!service.isAuthenticated());
    }
}
