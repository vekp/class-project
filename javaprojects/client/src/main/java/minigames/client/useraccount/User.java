package minigames.client.useraccount;

public class User {
    private String username;
    public User(){
        // needs to be empty for the JSON parser to work.
    }
    public User(String username){
        this.username = username;
    }    

    public String getUsername(){
        return this.username;
    }
}