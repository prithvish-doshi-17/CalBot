package com.se21.calbot.services;

import static com.se21.calbot.enums.Enums.calApiResponse.Success;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.se21.calbot.enums.Enums;
import com.se21.calbot.interfaces.Calendar;
import com.se21.calbot.model.AuthToken;
import com.se21.calbot.repositories.TokensRepository;
import com.se21.calbot.security.AuthTokenAuthenticationFilter;
import com.se21.calbot.security.AuthenticationService;

import lombok.extern.java.Log;

/**
 * Google Calendar operations are managed through this class.
 * It implements all functionalities of Calendar interface.
 */
@Service
@Log
public class GoogleCalendarService implements Calendar {

    @Autowired
    TokensRepository tokensRepository;
    @Autowired
    AuthTokenAuthenticationFilter authTokenAuthenticationFilter;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    GoogleClientSecrets clientSecrets;
    
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final  List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    CloseableHttpClient httpClient = HttpClients.createDefault();

    //This is temporary hack, I believe we need one more common authenticator block or something

    @Override
    public String authenticate(String discordId) {

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("offline")
                    .build();

            return flow.newAuthorizationUrl().setRedirectUri("http://localhost:8090/test").setState(discordId).build();
        } catch (Exception e) {
            log.severe("Google auth URL exception - " + e.getMessage());
        }

        return "Unable to authorize right now, please try again after sometime";
    }

    @Override
    public void saveAccessToken(String authCode, String discordId) throws Exception {
        try {
            String clientId = clientSecrets.getWeb().getClientId();
            String clientSecret = clientSecrets.getWeb().getClientSecret();

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            TokenResponse response = new GoogleAuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, clientId,
                    clientSecret, authCode, "http://localhost:8090/test")
                    .setGrantType("authorization_code").execute();

            if(response == null || response.getAccessToken() == null
                    || response.getExpiresInSeconds() == null
                    || response.getScope() == null) {
                log.severe("Invalid response from Google Token API");
                return;
            }
            
            AuthToken userToken = new AuthToken();
            userToken.setDiscordId(discordId);
            userToken.setToken(response.getAccessToken());
            userToken.setCode(authCode);
            userToken.setExpirydatetime(LocalDateTime.now().plusSeconds(response.getExpiresInSeconds()));
            userToken.setScope(response.getScope());

            //Once we have all the tokens we need to create a calendar to manage unscheduled events
            //Add condition if calendar already doesn't exist in the list of user accessible calendars
            if(StringUtils.isBlank(userToken.getCalId())) {
            	userToken.setCalId(createNewUnscheduledCalendar(response.getAccessToken()));
            }
            tokensRepository.save(userToken);
            
        } catch (Exception e) {
            log.severe("Google access token exception - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public org.json.JSONObject retrieveEvents(String calId) throws Exception {
        String access_token = authenticationService.getToken();
        String url = "https://www.googleapis.com/calendar/v3/calendars/" + calId + "/events?maxResults=20&access_token=" + access_token;
        HttpGet request = new HttpGet(url);
        //Todo: Add a filter to get events from now until end of current week only

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String content;
            if (entity != null) {
                content = EntityUtils.toString(entity);
                System.out.println(content);
                return new org.json.JSONObject(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Enums.calApiResponse updateEvents(org.json.JSONObject req) {
        return null;
    }

    public Enums.calApiResponse updateEvents(String eventId, String title, String hours){
        String url = "https://www.googleapis.com/calendar/v3/calendars/"+authenticationService.getCalId() + "/events/" + eventId;
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer "+authenticationService.getToken());
        request.setHeader("Content-Type", "application/json");
        StringEntity body = null;
        try {
            body = new StringEntity(this.createAddEventBody(title +"#"+ hours).toString());
            System.out.println(body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        body.setContentType("application/json");
        request.setEntity(body);
        System.out.println(request);
        try {
            httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Success;
    }
    public JSONObject createAddEventBody(DateTime startDt, DateTime endDt, String title)
    {
        JSONObject jsonEnd = new JSONObject();
        JSONObject jsonBody = new JSONObject();
        JSONObject jsonStart = new JSONObject();

        jsonEnd.put("dateTime", endDt.toString());
        jsonBody.put("end", jsonEnd);

        jsonStart.put("dateTime", startDt.toString());
        jsonBody.put("start", jsonStart);
        jsonBody.put("summary", title);
        return jsonBody;
    }

    public JSONObject createAddEventBody(String title)
    {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("summary", title);
        return jsonBody;
    }

    @Override
    public Enums.calApiResponse addEvents(String title, String hours, String deadline) throws Exception {
        DateTime startDt = DateTime.now();
        DateTimeFormatter f = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime endDt = f.parseDateTime(deadline);

        String url = "https://www.googleapis.com/calendar/v3/calendars/"+authenticationService.getCalId() + "/events";
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer "+authenticationService.getToken());
        request.setHeader("Content-Type", "application/json");

        StringEntity body = null;

        try {
            body = new StringEntity(this.createAddEventBody(startDt, endDt, title +"#"+ hours).toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw e;
        }

        body.setContentType("application/json");
        request.setEntity(body);

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();
            String content;
            if (entity != null) {
                content = EntityUtils.toString(entity);
                org.json.JSONObject obj = new org.json.JSONObject(content);
                System.out.println(obj);
                return Success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return Success;
    }

    @Override
    public Enums.calApiResponse deleteEvents() {
        return null;
    }


    public Enums.calApiResponse deleteEvents(String eventId) {
        String url = "https://www.googleapis.com/calendar/v3/calendars/"+authenticationService.getCalId() + "/events/" + eventId;
        HttpDelete request = new HttpDelete(url);
        request.setHeader("Authorization", "Bearer "+authenticationService.getToken());
        request.setHeader("Content-Type", "application/json");
        try {
            httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Success;
    }

    @Override
    public String createNewUnscheduledCalendar(String access_token) {
        String url = "https://www.googleapis.com/calendar/v3/calendars";
        HttpPost request = new HttpPost(url);
        StringEntity params = null;
        try {
            params = new StringEntity("{\"summary\":\"aPAS\"} ");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(params);
        request.setHeader("Authorization", "Bearer "+access_token);
        request.setHeader("Content-Type", "application/json");


        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String content;
            if (entity != null) {
                content = EntityUtils.toString(entity);
                System.out.println(content);
                org.json.JSONObject obj = new org.json.JSONObject(content);
                return obj.getString("id");//Need to store this id in Db
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
