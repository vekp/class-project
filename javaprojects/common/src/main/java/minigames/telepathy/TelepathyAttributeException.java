package minigames.telepathy;

public class TelepathyAttributeException extends RuntimeException {
    
    public TelepathyAttributeException(String attribute, String message){
        super(message + ".Attribute string given: " + attribute);
    }
}
