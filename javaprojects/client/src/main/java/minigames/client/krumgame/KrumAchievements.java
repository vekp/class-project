package minigames.client.krumgame;

enum achievements{
    SUICIDE_5, BAZOOKA_5, TARZAN_DEATH, LASER_DEATH, MAP_TRAP;

    @Override
    public String toString(){
        switch(this) {
            case SUICIDE_5:      return "Suicide x 5"; //User kills themselves 5 times
            case BAZOOKA_5:      return "Bazooka Kill x 5"; //User kills an opponent with a Bazooka 5 times
            case TARZAN_DEATH:   return "Tarzan Rope Death"; //User kills an opponent while shooting from a rope
            case LASER_DEATH:    return "Laser Death"; //Kill an opponent with a laser
            case MAP_TRAP:       return "COSC-120"; //Blow up a piece of a certain map (or each map)
            default:             return "Unknown Achievement";
        }
    }
}
