package minigames.server.spacemaze;

enum InteractiveResponses {
    GAME_STARTED,
    BOTS_COLLISION,
    ALL_KEY_OBTAINED,
    ONE_KEY_REMAINING,
    TWO_KEY_REMAINING,
    THREE_KEY_REMAINING,
    FOUR_KEY_REMAINING,
    DYNAMITE,
    NEW_LEVEL,
    WORM_HOLE,
    TREASURE_CHEST,
    ENOUGH_KEYS,
    START_DOOR;

    @Override
    public String toString(){
        switch (this){
            case GAME_STARTED:
                return "Journey begins Commander, The door awaits its key.";
            case BOTS_COLLISION:
                return "Beware of Bots Commander, That cost you one life.";
            case ALL_KEY_OBTAINED:
                return "LOOOK Commander, The door is OPEN!";
            case ONE_KEY_REMAINING:
                return "Almost there, Just one more key until victory!";
            case TWO_KEY_REMAINING:
                return "Halfway there, Two key to go!";
            case THREE_KEY_REMAINING:
                return "Three keys out in space maze, keep looking.";
            case FOUR_KEY_REMAINING:
                return "The quest is young. Four keys lie hidden.";
            case DYNAMITE:
                return "BOOOOM!!!!, Dynamite? In Space?";
            case NEW_LEVEL:
                return "Wait, we are still in the maze.";
            case WORM_HOLE:
                return "Wormhole? More like a surprise portal!";
            case TREASURE_CHEST:
                return "Commander, You time traveled 8 seconds in the past.";
            case ENOUGH_KEYS:
                return "We got what we need commander, Lets go!";
            default:
                return null;
        }
    }

}
