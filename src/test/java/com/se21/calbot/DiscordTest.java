package com.se21.calbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.se21.calbot.ClientManager.Discord;
import com.se21.calbot.controllers.Controller;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
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
	
	private static final String testId = "";
	private static final String msg = "test";
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
        String ID = "alex1011150849";
        String correctAddMsg = "!add SEclass 1 12/10/2021\n";

        // actual return data
        String actual = discord.processInput(ID, correctAddMsg);
        // create an expect result
        String expect = "done";

        System.out.println(actual);
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
        String ID = "alex1011150849";
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
        String ID = "alex1011150849";
        String eventMsg = "!event\n";

        // actual return data
        String actual = discord.processInput(ID, eventMsg);
        // create an expect result
        String expect = "Application Development Intern- Shao-Yo Chao    2021-09-03T10:30:00-04:00\n" ;
        //        "SEclass#1    2021-10-28T19:37:20Z\n";

        System.out.println(actual);
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
        String ID = "alex1011150849";
        String showMsg = "!show\n";

        // actual return data
        String actual = discord.processInput(ID, showMsg);
        // create an expect result
        String expect = "Application Development Intern- Shao-Yo Chao    2021-09-03T10:30:00-04:00\n";
        //        + "SEclass#1    2021-10-28T19:37:20Z\n";
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        System.out.println(timeStamp);
        System.out.println(actual);
        // check if correct
        assertEquals(expect, actual);
    }
}
