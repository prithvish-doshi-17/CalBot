package com.se21.calbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.se21.calbot.ClientManager.Discord;
import com.se21.calbot.controllers.Controller;

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

}
