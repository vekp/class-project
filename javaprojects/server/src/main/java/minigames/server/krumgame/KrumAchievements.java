package minigames.server.krumgame;

enum achievements{
    SUICIDE_5, BAZOOKA_5, TARZAN_DEATH, LASER_DEATH, MAP_SLAP, JOEY_SUICIDE, DIRECT_HIT, LONG_RANGE;

    @Override
    public String toString(){
        switch(this) {
            case SUICIDE_5:      return "Suicide x 5"; //User kills themselves 5 times
            case BAZOOKA_5:      return "Bazooka Kill x 5"; //User kills an opponent with a Bazooka 5 times
            case TARZAN_DEATH:   return "Tarzan Rope Death"; //User kills an opponent while shooting from a rope
            case LASER_DEATH:    return "Laser Death"; //Kill an opponent with a laser
            case MAP_SLAP:       return "Map Trap"; //Blow up a piece of a certain map (or each map)
            case JOEY_SUICIDE:   return "Joey Suicide"; // Kill yourself with a Joey
            case DIRECT_HIT:     return "Direct Hit with Grenade"; //Achievement for a Direct hit with a grenade, no bounce
            case LONG_RANGE:     return "Long Range Bazooka"; //Shot with a Bazooka over 600 pixels away
            default:             return "Unknown Achievement";
        }
    }
}
