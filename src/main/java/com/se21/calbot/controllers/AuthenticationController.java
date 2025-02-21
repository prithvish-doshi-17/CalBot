package com.se21.calbot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.interfaces.Calendar;

/**
 * This class serves as routing path controller. Any call to application/{route} will be handled here.
 * For this project we need to expose one endpoint for Google Oauth 2.0 to share auth code. So that is being handled by this class.
 */
@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    CalendarFactory calendarFactory;

    /**
     * This function will be executed whenever a call is made to https://{application endpoint}/test
     * It is responsible to extract auth token and user id from url parameters and save it in the db for future use.
     * @param code auth code
     * @param state userId
     * @return Msg for user
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String token(@RequestParam String code, @RequestParam String state) {
        Calendar calendar = calendarFactory.getCalendar("Google");
        try {
	        calendar.saveAccessToken(code, state);
	        return "Auth completed successfully, please close this window and get back to discord bot!";
        } catch(Exception e) {
        	return "Auth fail, please try it later or notify developer!";
        }
    }

    /**
     * Test endpoints, feel free to use them during development
     * @return url for Google OAuth2 authentication
     */
    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public String authenticateTest() {
        Calendar calendar = calendarFactory.getCalendar("Google");
        return calendar.authenticate("Xero978387");
    }

    /**
     * Test endpoints, feel free to use them during development
     */
    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public void summary(){
        Calendar calendar = calendarFactory.getCalendar("Google");
        //calendar.retrieveEvents(new JSONObject());
    }
}
