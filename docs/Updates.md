# Updates from previous version
## Commands

We have added commands like show, update and delete along with their functionality. Earlier the algorithm for the show command was not present. So we implemented it to display the optimized show output. Also the delete and update functionality was not present in previous version. So we added as well as tested the new functionalities. So we completed the CRUD functionalities of the project.

## 
## Bugs
Earlier version were having bugs in token authentications. Older version only access token is being used in code. In long term access token will get expired. So we need a mechanism to get new access token once it is expired by exchanging refresh token. So we complete the mechanism for refreshing tokens by implementing Spring Security.
We also provide a service bean for other java classes to access the user information instead of retrieving it from the database.
 

Also the older version was not able to avoid duplicate entries from the user. So we Added functionality to stop users from creating multiple events of the same name.
##
## Testing
We have created and updated a lot of testing files to avoid all of the previous version's failing test cases. 
Added unit tests to files and made them routinely execute on each push to master branch
Used code formatters and style checkers for the repository
Code coverage and automated analysis tools used to improve the quality of the repository.
