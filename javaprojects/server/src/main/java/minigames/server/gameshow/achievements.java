package minigames.server.gameshow;

enum achievements {
    WORD_SCRAMBLE, IMAGE_GUESSER, MEMORY, FIRST_CORRECT, FIRST_INCORRECT;

    @Override
    public String toString(){
        switch(this) {
            case WORD_SCRAMBLE:   return "Letter Jumbler Novice";
            case IMAGE_GUESSER:   return "Picasso in Disguise";
            case MEMORY:          return "Brain Boggler Beginner";
            case FIRST_CORRECT:   return "Genius Guesswork";
            case FIRST_INCORRECT: return "Oops, Oops, and More Oops";
            default:              return "Unknown Achievement";
        }
    }
}
