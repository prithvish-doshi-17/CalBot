package com.se21.calbot.interfaces;

import com.se21.calbot.enums.Enums;
import org.json.simple.JSONObject;

/**
 * This interface provides basic functionality for Calendar classes
 */
public interface Calendar {

    /**
     * It serves any authenticate request
     * @param id User unique identifier
     * @return URL for authentication
     */
    String authenticate(String id);

    /**
     * Saves received access token to db and perform some basic functionalities like getting
     * access token and refresh token further.
     * @param code auth code
     * @param id user Id
     * @throws Exception generic exception
     */
    void saveAccessToken(String code, String id) throws Exception;

    /**
     * Retrieves event from calendar
     * @param calId calendar id(One google calendar can have multiple calendars at same time)
     * @return JSON objects of calendar returned events
     * @throws Exception generic exception
     */
    org.json.JSONObject retrieveEvents(String calId) throws Exception;

    /**
     * Updates existing events to calendar
     * @param req type is JSON object, exact parameters not yet decided, but can be changed based on different type of calendars
     * @return response from calendar
     */
    Enums.calApiResponse updateEvents(JSONObject req);

    /**
     * Adds event to calendar, currently you can only add unscheduled
     * events i.e. when there is no start date but only deadline and number of hours need to be dedicated
     * @param title Event title
     * @param hours Number of hours to be dedicated
     * @param deadline deadline for this activity
     * @return Calendar response
     */
    Enums.calApiResponse addEvents( String title, String hours, String deadline) throws Exception;

    /**
     * Deletes one or some event
     * @return Calendar response
     */
    Enums.calApiResponse deleteEvents();

    Enums.calApiResponse deleteEvents(String eventId);

    /**
     * Create a new Unscheduled calendar in user's selected calendar
     * @param accessToken the token string from OAuth2
     * @return Calendar response
     */
    String createNewUnscheduledCalendar(String accessToken);

}
