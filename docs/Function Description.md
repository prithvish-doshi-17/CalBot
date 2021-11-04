## DiscordTest.java

##
DiscordTest Function:


 Create a token for test.  It return the mock service when calendarFactory is called in the mockController.
##
processInputWithoutAuthentication Function:

Return actual data. User the same data to get login url. Create an expect result and check if correct. When a user send a command to DiscordBot without authentication token,
	 the robot sends an url back to ask the user to login with OAuth2.
##
processInputAddCorrectly Function:

 When a user send a add command to DiscordBot with correct following,
     the robot sends string "Event added to your calendar!" back.
##
processInputAddWithDuplicateName Function:

When a user send a add command to DiscordBot with duplicate name,
    the robot sends string "Event already exists on your calendar" back.
##
processInputAddWithIllegalInput Function:

When a user send an add command to DiscordBot with illegal input,
      the robot sends string "done" back.
##
processInputEventWithCorrectInput Function:

  When a user send a event command to DiscordBot with correct input,
      the robot sends events back.
##
processInputOauthWithCorrectInput Function

When a user send a oauth command to DiscordBot with correct input,
      the robot sends an url back to ask the user to login with OAuth2 back.
##
processInputDeleteWithIllegalInput Function

   When a user send a delete command to DiscordBot with wrong input,
     the robot sends "Please type in the format: !delete title" back.


## Controller.java

arrangeEvents() Function:

This function contains the logic to re-arrange all events on the basis of priority and
     suggest most optimal ones for today/week. 
 * Firstly, the function filters out all the upcoming events of the next 7 days
     * Next, it calculates the difference of days between today and the deadline of that particular event
     * Based on these two values, the algorithm calculates the number of hours that are required to be dedicated today for any given event
     * These events (alongwith the required number of hours) is then displayed to the user.
##
deleteEvent(String title) Function:

 This function deletes the event mentioned by the user. As the bot does not allow duplicate events to be created, name of the event works as a unique identifier for this operation.
##
eventExists(String title)

  This function is a helper function to check if an event of a given name already exists or not. It is used as a checker before creating any new event.
##
updateEvent(String title, String hours, String deadline) Function

This function updates an existing event with updated number of hours and updated deadline
   . It has an inbuilt checker, so that it returns a message if the user tries to update a non-existent event.
##
String dataOperation(Enums.operationType opType, String ... msgParam) Function:

   This is single interface function for clientManager layer to directly call some
    . CRUD operation for calendar layer.
##
String getUrl(String discordId, String calType)

To get URL for OAUTH 2.0.
##
initToken(String id) Function:

authenticate the user token and put into SecurityContextHolder.

## pom.xml

Added plugin jacoco for code coverage. 
Added pmd, checkstyle, code formatter , syntax checkers plugins in pom.xml file.
## ControllerTest.java

initTokenShouldCreateTokenSuccessfully() Function:

When the function gets the id existing in the database, SecurityContextHolder will content an Authentication.
##
arrangeEvents() Function:

 When users pass a invalid code and state, the function should return a failure message
     In order not to test Google API, use the mock controller so that we can customize the return value of
      Google APIs.
##
getUrlShouldReturnOAuth2Link() Function:

When the calType is valid, return the OAuth2 link.

##
dataOperationAddShouldbeDone() Function:

When operating Enums.operationType.Add with correct parameters, return "Event added to your calendar!

##
dataOperationAddShouldReturnErrorMsg() Function:

After GoogleCalendarService throws exception, dataOperation will return error message.
##

dataOperationShouldReturnEventsString() Function:

When operating Enums.operationType.Retrieve, return events String from google API.
