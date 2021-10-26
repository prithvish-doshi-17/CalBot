package com.se21.calbot;

import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.services.GoogleCalendarService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The tests for {@link com.se21.calbot.controllers.AuthenticationController AuthenticationController.java}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    CalendarFactory calendarFactory;

    @Mock
    private GoogleCalendarService service;

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.AuthenticationController#token(String, String) token(String, String)}
     * </p>
     * When users pass a invalid code and state, the function should return a failure message
     *
     * @throws Exception exception from perform()
     */
    @Test
    public void tokenShouldReturnFailureMessage() throws Exception {
        mvc.perform(get("/test?code=&state="))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Auth fail, please try it later or notify developer!"));
    }

    /**
     * TODO need to find some way to get code and state from OAuth2
     * <p>
     * Test target: {@link com.se21.calbot.controllers.AuthenticationController#token(String, String) token(String, String)}
     * </p>
     * When users pass a valid code and state, the function should return a successful message
     *
     * @throws Exception exception from perform()
     */
    @Test
    public void tokenShouldReturnSuccessMessage() throws Exception {
        mvc.perform(get("/test?code=&state="))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Auth completed successfully, please close this window and get back to discord bot!"));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.AuthenticationController#authenticateTest authenticateTest()}
     * </p>
     * Test if the function return correct url for OAuth2
     *
     * @throws Exception exception from perform()
     */
    @Test
    public void authenticateTestShouldReturnUrl() throws Exception {
        mvc.perform(get("/generate"))
                .andExpect((content().string(containsString("state=Xero978387"))));
    }
}
