package minigames.server.spacemaze;

enum InteractiveResponses {
    GAME_STARTED,
    BOTS_COLLISION,
    ALL_KEY_OBTAINED,
    ONE_KEY_REMAINING,
    TWO_KEY_REMAINING,
    DYNAMITE,
    NEW_LEVEL,
    WORM_HOLE;

    @Override
    public String toString(){
        switch (this){
            case GAME_STARTED:
                return "The Door is locked, Look for the Key!";
            case BOTS_COLLISION:
                return "Oh No! You've lost a life! Avoid BOTS!";
            case ALL_KEY_OBTAINED:
                return "You have all the keys, Head to the door!";
            case ONE_KEY_REMAINING:
                return "You have to collect 1 more key, Keep Looking!";
            case TWO_KEY_REMAINING:
                return "You have to collect 2 more keys, Keep Looking!";
            case DYNAMITE:
                return "BOOOOM!!!!, The walls are destroyed!!!";
            case NEW_LEVEL:
                return "Congratulation!, You've reached a new level!";
            case WORM_HOLE:
                return "You entered the worm hole, wait, Where are you? ";
            default:
                return null;
        }
    }

}
