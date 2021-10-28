package com.se21.calbot;

import com.se21.calbot.controllers.Controller;
import com.se21.calbot.enums.Enums;
import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.security.AuthTokenAuthenticationFilter;
import com.se21.calbot.security.AuthenticationService;
import com.se21.calbot.services.GoogleCalendarService;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

/**
 * The tests for {@link com.se21.calbot.controllers.Controller Controller.java}
 */
@SpringBootTest(classes = SpringUnitTestConfig.class)
public class ControllerTest {

    @Autowired
    TokensRepository tokensRepository;
    @Autowired
    AuthTokenAuthenticationFilter authTokenAuthenticationFilter;
    @Autowired
    @Spy
    // create a spy on this authenticationService.
    // When authenticationService is called, this object will be called
    AuthenticationService authenticationService;

    // prepare the mock bean for the mockController
    @Mock
    GoogleCalendarService googleCalendarService;
    @Mock
    CalendarFactory calendarFactory;
    private final String calenderApiMockReturn = "{" +
            "items:[{summary: 'test1', start:{dateTime:'2021/1/1'}}, " +
            "{summary: 'test2', start:{ dateTime:'2021/1/1'}}]}";
    @InjectMocks
    Controller mockController;

    @Autowired
    Controller controller;

    private static final String existId = "test";
    private static final String nonExistId = "test1";

    @BeforeEach
    void init() {
        // create a token for test
        AuthToken token = new AuthToken();
        token.setDiscordId(existId);
        token.setToken("test");
        token.setCalId("test");
        token.setExpirydatetime(LocalDateTime.now().plusMinutes(10));
        tokensRepository.save(token);

        // return the mock service when calendarFactory is called in the mockController
        doReturn(googleCalendarService).when(calendarFactory).getCalendar("Google");
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#initToken(String) initToken(String)}
     * </p>
     * When the function gets the id existing in the database, SecurityContextHolder will content an Authentication
     */
    @Test
    public void initTokenShouldCreateTokenSuccessfully() {
        authTokenAuthenticationFilter.doFilter(existId);
        controller.initToken(existId);
        assertTrue(SecurityContextHolder.getContext().getAuthentication() != null);
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#initToken(String) initToken(String)}
     * </p>
     * When the function gets the id existing in the database, SecurityContextHolder will content an Authentication
     */
    @Test
    public void initTokenShouldNotCreateTokenSuccessfully() {
        controller.initToken(nonExistId);
        assertTrue(SecurityContextHolder.getContext().getAuthentication() == null);
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#arrangeEvents() arrangeEvents()}
     * </p>
     * When users pass a invalid code and state, the function should return a failure message
     * In order not to test Google API, use the mock controller so that we can customize the return value of
     * Google APIs.
     *
     * @throws Exception exception from arrangeEvents(), retrieveEvents()
     */
    @Test
    public void arrangeEvents() throws Exception {
        // init the Spring Security Token
        authTokenAuthenticationFilter.doFilter(existId);
        // mock the return result of googleCalendarService
        doReturn(new JSONObject(calenderApiMockReturn)).when(googleCalendarService).retrieveEvents(anyString());

        assertTrue(mockController.arrangeEvents().equals(
                "test1    2021/1/1\n" +
                        "test2    2021/1/1\n" +
                        "test1    2021/1/1\n" +
                        "test2    2021/1/1\n"));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#getUrl(String, String) getUrl(String, String)}
     * </p>
     * When the calType is valid, return the OAuth2 link
     */
    @Test
    public void getUrlShouldReturnOAuth2Link() throws Exception {
        // mock a return result of googleCalendarService
        doReturn("http://localhost:8080/test").when(googleCalendarService).authenticate(anyString());
        assertTrue(controller.getUrl(existId, "Google").contains("test"));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#getUrl(String, String) getUrl(String, String)}
     * </p>
     * When the calType is invalid, return an empty String
     */
    @Test
    public void getUrlShouldNotReturnOAuth2Link() throws Exception {
        // mock a return result of googleCalendarService
        doReturn("http://localhost:8080/test").when(googleCalendarService).authenticate(anyString());
        assertTrue(Strings.isBlank(controller.getUrl("test", "Facebook")));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#dataOperation(Enums.operationType, String...) dataOperation(Enums.operationType, String...))}
     * </p>
     * When operating Enums.operationType.Add with correct parameters, return "done"
     */
    @Test
    public void dataOperationShouldReturnDone() throws Exception {
        // mock a return result of googleCalendarService
        doReturn(Enums.calApiResponse.Success).when(googleCalendarService).addEvents(anyString(), anyString(), anyString());
        assertTrue(mockController.dataOperation(Enums.operationType.Add, "title", "hours", "deadline").equals("done"));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#dataOperation(Enums.operationType, String...) dataOperation(Enums.operationType, String...))}
     * </p>
     * When operating Enums.operationType.Delete, Create, Update, return "failure"
     */
    @Test
    public void dataOperationShouldReturnFailure() throws Exception {
        assertTrue(mockController.dataOperation(Enums.operationType.Delete, "title", "hours", "deadline").equals("Failure!"));
        assertTrue(mockController.dataOperation(Enums.operationType.Create, "title", "hours", "deadline").equals("Failure!"));
        assertTrue(mockController.dataOperation(Enums.operationType.Update, "title", "hours", "deadline").equals("Failure!"));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#dataOperation(Enums.operationType, String...) dataOperation(Enums.operationType, String...))}
     * </p>
     * When operating Enums.operationType.Retrieve, return events String from google API
     */
    @Test
    public void dataOperationShouldReturnEventsString() throws Exception {
        // init the Spring Security Token
        authTokenAuthenticationFilter.doFilter(existId);
        // mock the return result of googleCalendarService
        doReturn(new JSONObject(calenderApiMockReturn)).when(googleCalendarService).retrieveEvents(anyString());

        assertTrue(mockController.dataOperation(Enums.operationType.Retrieve).equals(
                "test1    2021/1/1\n" +
                "test2    2021/1/1\n" +
                "test1    2021/1/1\n" +
                "test2    2021/1/1\n"));
    }
}
