# FileHandler

## FileHandler Class

The `FileHandler` class is responsible for managing user account data stored in JSON files. It provides methods for generating user accounts, retrieving lists of usernames, accessing game information, adding games, and adding sessions. All the other classes `Game`, `Session`, and `UserData` are used by the `FileHandler` class to store and retrieve data. The only class that is needed is the FileHandler class to interact with the file system.

### Class Details

#### FileHandler

- **Constructor:** `FileHandler(String username)`
  - Initializes a `FileHandler` object with the specified `username`.

### Methods

```java

// generateUser creates a new user account with the specified username and pin
public void generateUser(String username, String pin)

FileHandler fileHandler = new FileHandler("myUsername");
fileHandler.generateUser("myUsername", "1234");



// retrieves a list of usernames
public ArrayList<String> listOfUsernames()

FileHandler fileHandler = new FileHandler("myUsername");

ArrayList<String> usernames = fileHandler.listOfUsernames();



// To store game information in a JSON file for a user firstly the current user must be retrieved from the server


// retrieves the current user from the server, a variable of type MiniGameNetworkClient is required this retrieves a server instance
private MinigameNetworkClient userClient;

Future<String> userFromServer = userClient.userNameGet();

// the user is retrieved from the server a Future value is returned Futures can only be accessed in the onSuccess execution context

// For example this is how to access the user from the server
userFromServer.onSuccess(
    userFromServerResult -> {
        // this creates a new instantiation of the FileHandler class with the retrieved user
        FileHandler user = new FileHandler(userFromServerResult);
        // this will find the active users latest session and add this to the users JSON file
        user.addGame("myGame", "myGameDescription");
    })

```
