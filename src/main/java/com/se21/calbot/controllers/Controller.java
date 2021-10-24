package com.se21.calbot.controllers;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se21.calbot.enums.Enums;
import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.interfaces.Calendar;
import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.security.AuthTokenAuthenticationFilter;
import com.se21.calbot.security.AuthenticationService;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;


/**
 * This is main controller class for whole aPAS. In block diagram of architecture, you can see this
 * class at the center. It serves various purposes, but major functionality is to provide some
 * algorithm to suggest most optimal activities to be done by user.
 */
@Log
@Getter
@Setter
@Service
public class Controller {

    String clientId;
    Enums.calendarType calendarId;
    String calendarToken; //check later
    Enums.operationType operationId;
    Calendar calObj;

    @Autowired
    CalendarFactory calendarFactory;
    @Autowired
    TokensRepository tokensRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    AuthTokenAuthenticationFilter authTokenAuthenticationFilter;

    /**
     * This function contains the logic to re-arrange all events on the basis of priority and
     * suggest most optimal ones for today/week.
     * @return arrange result
     * @throws Exception generic exception 
     */
    //TODO: Change return type to JSON objects
    public String arrangeEvents() throws Exception {
        JSONArray scheduledEventList = calObj.retrieveEvents("primary").getJSONArray("items");
        JSONArray unScheduledEventList = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");

        //Filter events for this week


        //Make a list of all events

        //Prioritise all unscheduled events


        //Send the list to client to display
        String events = "";
        for (int i = 0; i < scheduledEventList.length(); i++) {
            org.json.JSONObject jsonLineItem = scheduledEventList.getJSONObject(i);
            events += jsonLineItem.getString("summary") + "    " + jsonLineItem.getJSONObject("start").getString("dateTime") + "\n";
        }
        // Unscheduled events set in bot created aPAS calendar
        for (int i = 0; i < unScheduledEventList.length(); i++) {
            org.json.JSONObject jsonLineItem = unScheduledEventList.getJSONObject(i);
            events += jsonLineItem.getString("summary") + "    " + jsonLineItem.getJSONObject("start").getString("dateTime") + "\n";
        }
        return events;
    }

    /**
     * This is single interface function for clientManager layer to directly call some
     * CRUD operation for calendar layer.
     * @param opType Add, delete, Retrieve.......etc.
     * @param msgParam the user given parameters
     * @return Output received after operation execution
     */
    //Todo return type need to be changed to Json objects to make controller and client independent
    @SneakyThrows
    public String dataOperation(Enums.operationType opType, String ... msgParam){
        switch(opType)
        {
            case Add:
            {
                if(msgParam.length != 3)
                {
                    //Some exception is needed and need to indicate user to enter in correct format
                }

                calObj.addEvents(msgParam[0], msgParam[1], msgParam[2]);//!Add title hoursNeeded deadline
                return "done";
            }

            case Delete:
            case Create:
            case Update:
                break;
            case Optimise:
            {
                return this.arrangeEvents();
            }

            case Retrieve:
            {
                try {
                    //  Scheduled events set in primary calendar
                    JSONArray itemArray = calObj.retrieveEvents("primary").getJSONArray("items");
                    String events= "";
                    for (int i = 0; i < itemArray.length(); i++) {
                        org.json.JSONObject jsonLineItem = itemArray.getJSONObject(i);
                        events += jsonLineItem.getString("summary") + "    " + jsonLineItem.getJSONObject("start").getString("dateTime") + "\n";
                    }
                    // Unscheduled events set in bot created aPAS calendar
                    itemArray = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");
                    for (int i = 0; i < itemArray.length(); i++) {
                        org.json.JSONObject jsonLineItem = itemArray.getJSONObject(i);
                        events += jsonLineItem.getString("summary") + "    " + jsonLineItem.getJSONObject("start").getString("dateTime") + "\n";
                    }
                    return events;
                } catch (Exception e) {
                    log.severe("Google auth URL exception - " + e.getMessage());
                }
            }

            break;
            default:
                throw new IllegalStateException("Unexpected value: " + opType);
        }
        return "Failure!";
    }

    /**
     * To get URL for OAUTH 2.0
     * @param discordId primary key of Token table
     * @param calType calendar type
     * @return URL
     */
    public String getUrl(String discordId, String calType) {
        try {
            return calendarFactory.getCalendar(calType).authenticate(discordId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }
    
    /**
     * 
     * authenticate the user token and put into SecurityContextHolder
     * @param id the index of Table Token in database
     */
    public void initToken(String id) {
    	authTokenAuthenticationFilter.doFilter(id);
    }
}

