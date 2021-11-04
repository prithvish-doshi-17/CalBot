package com.se21.calbot.controllers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se21.calbot.enums.Enums;
import com.se21.calbot.factories.CalendarFactory;
import com.se21.calbot.interfaces.Calendar;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.security.AuthTokenAuthenticationFilter;
import com.se21.calbot.security.AuthenticationService;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.time.Duration;
import java.time.LocalDateTime;


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
    /**
     * Firstly, the function filters out all the upcoming events of the next 7 days
     * Next, it calculates the difference of days between today and the deadline of that particular event
     * Based on these two values, the algorithm calculates the number of hours that are required to be dedicated today for any given event
     * These events (alongwith the required number of hours) is then displayed to the user
     */
    public String arrangeEvents() throws Exception {
        calObj = calendarFactory.getCalendar("Google");
        JSONArray scheduledEventList = calObj.retrieveEvents("primary").getJSONArray("items");
        JSONArray unScheduledEventList = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");
        JSONArray eventsToSchedule = new JSONArray();
        String eventsThisWeek = "Here are the events for this week:\n";

        //Filter events for this week
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        float hours = 0.0F;
        for (int i = 0; i < unScheduledEventList.length(); i++) {
            org.json.JSONObject jsonLineItem = unScheduledEventList.getJSONObject(i);
            java.time.LocalDateTime event_deadline;
            event_deadline = LocalDateTime.parse(jsonLineItem.getJSONObject("end").getString("dateTime").substring(0, 19));
            Duration difference = Duration.between(now, event_deadline);
            int days = (int) difference.toDays();

            if (days <= 7 && days >= 0) {
                String[] eventProperties = jsonLineItem.getString("summary").split("#");
                eventsToSchedule.put(jsonLineItem);
                hours = hours + Float.parseFloat(eventProperties[1]);
                eventsThisWeek += jsonLineItem.getString("summary") + "    Deadline:(" +
                        jsonLineItem.getJSONObject("end").getString("dateTime") + ",TZ:" +
                        jsonLineItem.getJSONObject("end").getString("timeZone") + ")\n";
            }
        }

        //Prioritise all unscheduled events
        eventsThisWeek += "\nHere are the activities to do for today (and the number of hours to be dedicated):\n";
        for (int i = 0; i < eventsToSchedule.length(); i++)
        {
            org.json.JSONObject jsonLineItem = eventsToSchedule.getJSONObject(i);
            java.time.LocalDateTime event_deadline;
            event_deadline = LocalDateTime.parse(jsonLineItem.getJSONObject("end").getString("dateTime").substring(0, 19));
            Duration difference = Duration.between(now, event_deadline);
            int days = (int) difference.toDays();
            String[] eventProperties = jsonLineItem.getString("summary").split("#");
            float numberOfHours = (float) Math.ceil(Float.parseFloat(eventProperties[1]) * 2 / days) / 2;
            eventsThisWeek += eventProperties[0] + " (Number of hours: " + numberOfHours + ")\n";
        }

        return eventsThisWeek;
    }
    
    /**
    * This function deletes the event mentioned by the user
    * As the bot does not allow duplicate events to be created, name of the event works as a unique identifier for this operation
    */
    public String deleteEvent(String title) throws Exception {
        calObj = calendarFactory.getCalendar("Google");
        JSONArray unScheduledEventList = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");

        for (int i = 0; i < unScheduledEventList.length(); i++) {
            org.json.JSONObject jsonLineItem = unScheduledEventList.getJSONObject(i);
            String[] eventProperties = jsonLineItem.getString("summary").split("#");
            if (title.equals(eventProperties[0]))
            {
                calObj.deleteEvents(jsonLineItem.getString("id"));
                return "Event deleted successfully!";
            }
        }
        return "Please enter correct title for the event to be deleted";
    }
    
    /**
    * This function is a helper function to check if an event of a given name already exists or not
    * It is used as a checker before creating any new event
    */
    public boolean eventExists(String title) throws Exception {
        calObj = calendarFactory.getCalendar("Google");
        JSONArray unScheduledEventList = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");

        for (int i = 0; i < unScheduledEventList.length(); i++) {
            org.json.JSONObject jsonLineItem = unScheduledEventList.getJSONObject(i);
            String[] eventProperties = jsonLineItem.getString("summary").split("#");
            if (title.equals(eventProperties[0]))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
    * This function updates an existing event with updated number of hours and updated deadline
    * It has an inbuilt checker, so that it returns a message if the user tries to update a non-existent event
    */
    public String updateEvent(String title, String hours, String deadline) throws Exception {
        calObj = calendarFactory.getCalendar("Google");
        JSONArray unScheduledEventList = calObj.retrieveEvents(authenticationService.getCalId()).getJSONArray("items");
        for (int i = 0; i < unScheduledEventList.length(); i++) {
            JSONObject jsonLineItem = unScheduledEventList.getJSONObject(i);
            String[] eventProperties = jsonLineItem.getString("summary").split("#");
            if (title.equals(eventProperties[0]))
            {
                calObj.deleteEvents(jsonLineItem.getString("id"));
                calObj.addEvents(eventProperties[0], hours, deadline);
                return "Event updated successfully!";
            }
        }
        return "Please enter correct title for the event to be updated";
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
        calObj = calendarFactory.getCalendar("Google");
        switch(opType)
        {

            /*
              This case first checks if an event already exists by the given name. If yes, it returns a message
              Else, it calls the function to create a new event on the user's calendar
             */
            case Add:
            {
                try {
                    if (eventExists(msgParam[0]))
                        return "Event already exists on your calendar";
                    else
                    {
                        //The format should be: title hoursNeeded deadline
                        calObj.addEvents(msgParam[0], msgParam[1], msgParam[2]);//!Add title hoursNeeded deadline
                        return "Event added to your calendar!";
                    }
                } catch (Exception e) {
                    //Throw exception if any part did not follow the format
                    System.out.println(e.getMessage());
                    return "Please type in the format: !add title hours mm/dd/yyyy";
                }

            }

            /*
              This case calls the function to delete a particular event from the user's calendar
             */
            case Delete:
            {
                try {
                    return this.deleteEvent(msgParam[0]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return "Please type in the format: !delete title";
                }
            }
            case Create:

            /*
              This case calls the function to update a particular event in the user's calendar
             */
            case Update:
            {
                try {
                    return this.updateEvent(msgParam[0], msgParam[1], msgParam[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return "Please type in the format: !update title hours";
                }
            }

            /*
              This case gives the output of the scheduler algorithm based on number of hours, deadline, etc.
             */
            case Optimise:
            {
                return this.arrangeEvents();
            }

            /*
              This case retrieves all the events present in the user's calendar
             */
            case Retrieve:
            {
                try {
                    //  Scheduled events set in primary calendar
                    JSONArray itemArray = calObj.retrieveEvents("primary").getJSONArray("items");
                    String events= "Here are all the upcoming events on your calendar:\n";
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

