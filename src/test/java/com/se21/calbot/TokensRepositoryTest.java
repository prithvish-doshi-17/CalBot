package com.se21.calbot;


import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertTrue;

/**
 * The tests for {@link com.se21.calbot.repositories.TokensRepository TokensRepository.java}
 * Because this test uses the datasource, remember start the database.
 */
@SpringBootTest(classes = SpringUnitTestConfig.class)
public class TokensRepositoryTest {
    @Autowired
    TokensRepository tokensRepository;

    private AuthToken token;

    @BeforeEach
    void initDB(){
        this.token = new AuthToken();
        token.setDiscordId("test");
        token.setToken("test");
        tokensRepository.save(token);
    }


    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.repositories.TokensRepository#findById(Object) findById(Object)}
     * </p>
     * Test the datasource is working and check if data is the same.
     */
    @Test
    void findByIdShouldReturnData() {
        // actual return data
        AuthToken actual = tokensRepository.findById("test").orElse(new AuthToken());
        String actualToken = actual.getToken();

        Assertions.assertTrue(this.token.getToken().equals(actualToken));
    }
}
