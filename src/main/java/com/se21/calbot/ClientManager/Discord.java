package com.se21.calbot.ClientManager;

import static com.se21.calbot.enums.Enums.operationType.Add;
import static com.se21.calbot.enums.Enums.operationType.Optimise;
import static com.se21.calbot.enums.Enums.operationType.Retrieve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se21.calbot.controllers.Controller;
import com.se21.calbot.interfaces.ClientManager;
import com.se21.calbot.security.AuthenticationService;

/**
 * Discord class manages implementation specific to discord users
 */
@Service
public class Discord implements ClientManager {

	@Autowired
	Controller controller;
	@Autowired
	AuthenticationService authenticationService;

	@Override
	public void update() {

	}

	@Override
	public void add() {

	}

	@Override
	public void delete() {

	}

	@Override
	public void show() {

	}

	@Override
	public void suggestForNow() {

	}

	@Override
	public void suggestWeeklyUnscheduled() {

	}

	@Override
	public String processInput(String clientId, String msg) {
		controller.initToken(clientId);
		if (!authenticationService.isAuthenticated()) {
			String url = controller.getUrl(clientId, "Google");
			return "Please authorise aPAS to use your calendar\n" + url + "\n"
					+ "Once done please proceed with below commands" + "\n\n" + "!event: To see your scheduled events\n"
					+ "!add taskName hoursNeeded deadline(mm/dd/yyy) : To add new event with given details\n"
					+ "!show: To display tasks you should perform today considering priority of all events";
		}

		String[] commands = msg.split("\n");
		for (String eachCommand : commands) {
			String[] tokens = eachCommand.split(" ");
			switch (tokens[0]) {
			case "!add": {
				// The add logic will be added into add function later on.
				return controller.dataOperation(Add, tokens[1], tokens[2], tokens[3]);
			}
			case "!event": {
				return controller.dataOperation(Retrieve);
			}
			case "!oauth": {
				String url = controller.getUrl(clientId, "Google");
				return "Please authorise aPAS to use your calendar\n" + url + "\n"
						+ "Once done please proceed with below commands" + "\n\n"
						+ "!event: To see your scheduled events\n"
						+ "!add taskName hoursNeeded deadline(mm/dd/yyy) : To add new event with given details\n"
						+ "!show: To display tasks you should perform today considering priority of all events";
			}
			case "!show": {
				return controller.dataOperation(Optimise);
			}
			default:
				return "The command '" + tokens[0] + "' is undefined. Please use the command as follow:\n"
						+ "!event: To see your scheduled events\n"
						+ "!add taskName hoursNeeded deadline(mm/dd/yyy) : To add new event with given details\n"
						+ "!show: To display tasks you should perform today considering priority of all events";
			}

		}
		return "";
	}

	@Override
	public void getLastMessageDataObject() {

	}
}
