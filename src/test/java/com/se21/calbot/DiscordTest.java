package com.se21.calbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.services.GoogleCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.se21.calbot.ClientManager.Discord;
import com.se21.calbot.controllers.Controller;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
/**
 * The tests for {@link com.se21.calbot.ClientManager.Discord Discord.java}
 */
@SpringBootTest(classes = SpringUnitTestConfig.class)
public class DiscordTest {
	@Autowired
	Discord discord;
	@Autowired
	Controller controller;
    @Autowired
    TokensRepository tokensRepository;

    @Mock
    GoogleCalendarService googleCalendarService;
    @Mock
    CalendarFactory calendarFactory;
    private final String calenderApiMockReturn = "{" +
            "items:[{summary: 'test1', start:{dateTime:'2021/1/1'}}, " +
            "{summary: 'test2', start:{ dateTime:'2021/1/1'}}]}";
    @Mock
    Discord mockDiscord;
    @InjectMocks
    Controller mockController;
    private static final String existId = "test";
    private static final String nonExistId = "test1";
    private static final String testId = "";
    private static final String msg = "test";

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
	 * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
	 * </p>
	 * When a user send a command to DiscordBot without authentication token,
	 * the robot sends an url back to ask the user to login with OAuth2.
	 */
	@Test
	void processInputWithoutAuthentication() {
		// actual return data
		String actual = discord.processInput(testId, msg);
		
		// user the same data to get login url
		String url = controller.getUrl(testId, "Google");
		// create an expect result
		String expect = "Please authorise aPAS to use your calendar\n" + url + "\n"
				+ "Once done please proceed with below commands" + "\n\n" + "!event: To see your scheduled events\n"
				+ "!add taskName hoursNeeded deadline(mm/dd/yyy) : To add new event with given details\n"
				+ "!show: To display tasks you should perform today considering priority of all events";
		
		// check if correct
		assertEquals(expect, actual);
	}
    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
     * </p>
     * When a user send a add command to DiscordBot with correct following,
     * the robot sends string "done" back.
     */
    @Test
    void processInputAddCorrectly() {
        String ID = "test";
        String correctAddMsg = "!add SEclass 1 12/10/2021\n";

        // actual return data
        String actual = discord.processInput(ID, correctAddMsg);
        // create an expect result
        String expect = "done";
        // check if correct
        assertEquals(expect, actual);
    }

    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
     * </p>
     * When a user send an add command to DiscordBot with illegal input,
     * the robot sends string "done" back.
     */
    @Test
    void processInputAddWithIllegalInput() {
        String ID = "test";
        String correctAddMsg = "!add SEclass 12/01/2021T10:30\n";

        // actual return data
        String actual = discord.processInput(ID, correctAddMsg);
        // create an expect result
        String expect = "Please type in the format: !add title hours mm/dd/yyyy";

        System.out.println(actual);
        // check if correct
        assertEquals(expect, actual);
    }

    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
     * </p>
     * When a user send a event command to DiscordBot with correct input,
     * the robot sends events back.
     */
    @Test
    void processInputEventWithCorrectInput() {
        String ID = "test";
        String eventMsg = "!event\n";

        doReturn("SEclass 1 12/10/2021").when(mockDiscord).processInput(ID, eventMsg);

        // mocked actual return data
        String actual = mockDiscord.processInput(ID, eventMsg);
        // create an expect result
        String expect = "SEclass 1 12/10/2021";

        // check if correct
        assertEquals(expect, actual);
    }

    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
     * </p>
     * When a user send a oauth command to DiscordBot with correct input,
     * the robot sends an url back to ask the user to login with OAuth2 back.
     */
    @Test
    void processInputOauthWithCorrectInput() {
        String actual = discord.processInput(testId, msg);

        // user the same data to get login url
        String url = controller.getUrl(testId, "Google");
        // create an expect result
        String expect = "Please authorise aPAS to use your calendar\n" + url + "\n"
                + "Once done please proceed with below commands" + "\n\n" + "!event: To see your scheduled events\n"
                + "!add taskName hoursNeeded deadline(mm/dd/yyy) : To add new event with given details\n"
                + "!show: To display tasks you should perform today considering priority of all events";

        // check if correct
        assertEquals(expect, actual);
    }

    /**
     * <p>
     * 	Test target: {@link com.se21.calbot.ClientManager.Discord#processInput(String, String) processInput(String, String)}
     * </p>
     * When a user send a show command to DiscordBot with correct input,
     * the robot sends events back.
     */
    @Test
    void processInputShowWithCorrectInput() {
        String ID = "test";
        String showMsg = "!show\n";

        doReturn("SEclass 1 12/10/2021").when(mockDiscord).processInput(ID, showMsg);

        // mocked actual return data
        String actual = mockDiscord.processInput(ID, showMsg);
        // create an expect result
        String expect = "SEclass 1 12/10/2021";

        // check if correct
        assertEquals(expect, actual);
    }
}
