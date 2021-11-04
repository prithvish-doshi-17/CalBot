package com.se21.calbot;

import com.se21.calbot.controllers.Controller;
import com.se21.calbot.enums.Enums;
import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.interfaces.Calendar;
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

import javax.sound.midi.SysexMessage;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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
            "items:[{summary: '#1', start:{dateTime:'" + now.toString() + "'}, end:{dateTime:'" + now.toString() + "', timeZone: 'UTF+0'}}, " +
            "{summary: '#2', start:{dateTime:'" + now.toString() + "'},end:{ dateTime:'" + now.toString() + "', timeZone: 'UTF+0'}}]}";
    @InjectMocks
    Controller mockController;

    @Autowired
    Controller controller;

    private static final String existId = "test";
    private static final String nonExistId = "test1";
    private static final LocalDateTime now = LocalDateTime.now().plusDays(2);

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
                "Here are the events for this week:\n" +
                        "#1    Deadline:("+now.toString()+",TZ:UTF+0)\n" +
                        "#2    Deadline:("+now.toString()+",TZ:UTF+0)\n" +
                        "\n" +
                        "Here are the activities to do for today (and the number of hours to be dedicated):\n" +
                        " (Number of hours: 1.0)\n" +
                        " (Number of hours: 2.0)\n"));
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
        doReturn("http://localhost:8090/test").when(googleCalendarService).authenticate(anyString());
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
        doReturn("http://localhost:8090/test").when(googleCalendarService).authenticate(anyString());
        assertTrue(Strings.isBlank(controller.getUrl("test", "Facebook")));
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#dataOperation(Enums.operationType, String...) dataOperation(Enums.operationType, String...))}
     * </p>
     * When operating Enums.operationType.Add with correct parameters, return "Event added to your calendar!"
     */
    @Test
    public void dataOperationAddShouldbeDone() throws Exception {
        // mock a return result of googleCalendarService
        authTokenAuthenticationFilter.doFilter(existId);
        doReturn(Enums.calApiResponse.Success).when(googleCalendarService).addEvents(anyString(), anyString(), anyString());
        doReturn(new JSONObject(calenderApiMockReturn)).when(googleCalendarService).retrieveEvents(anyString());
        // create an actual and expect result
        String actual = mockController.dataOperation(Enums.operationType.Add, "title", "1", now.toString());
        String expect = "Event added to your calendar!";
        assertEquals(expect, actual);
    }

    /**
     * <p>
     * Test target: {@link com.se21.calbot.controllers.Controller#dataOperation(Enums.operationType, String...) dataOperation(Enums.operationType, String...))}
     * </p>
     * after GoogleCalendarService throws exception, dataOperation will return error message
     */
    @Test
    public void dataOperationAddShouldReturnErrorMsg() throws Exception {
        // mock a return result of googleCalendarService
        doThrow(new Exception()).when(googleCalendarService).addEvents(anyString(), anyString(), anyString());
        assertEquals(mockController.dataOperation(Enums.operationType.Add, "title", "hours", "deadline")
                , "Please type in the format: !add title hours mm/dd/yyyy");
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
        // create an actual and expect result
        String actual = mockController.dataOperation(Enums.operationType.Retrieve);
        String expect = "Here are all the upcoming events on your calendar:\n" +
                "#1    " + now.toString() + "\n" +
                "#2    " + now.toString() + "\n" +
                "#1    " + now.toString() + "\n" +
                "#2    " + now.toString() + "\n";
        assertEquals(expect, actual);
    }
}
