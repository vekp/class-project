package minigames.server.telepathy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A class representing a Telepathy board including a method to access Square objects
 */

public class Board{

    public Square[][] board; 
    int ROWS = 9; // adjustable variable for grid size
    int COLS = 9; // adjustable variable for grid size

    /**
     * board constructor
     */
    public Board(){
        this.board = generateBoard(ROWS, COLS);
    }

    //getter

    public Square[][] getBoard(){return this.board;}


    /**
     * A method to generate a 2D array of square objects
     * @param rows an int representing number of rows on the board
     * @param cols an int representing number of columns on the board
     * @return a 2D array of Square objects
     */
    public Square[][] generateBoard(int rows, int cols) {
        Square[][] board = new Square[rows][cols];
        Random randNumber = new Random(16); // every board will be the same because of 'seed' number in formula
        ArrayList<Object> colourList = generateColours();
        ArrayList<Object> symbolList = generateSymbols();
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board.length; col++) {
                Colours colour = (Colours) colourList.get(randNumber.nextInt(9));
                Symbols symbol = (Symbols) symbolList.get(randNumber.nextInt(9));
                Square square = new Square(row, col, colour, symbol);
                board[row][col] = square;
                //redundant code: used to quickly check random allocation was functioning
                //int x = square.getHorizontalPos();
                //int y = square.getVerticalPos();
                //Colours squareColour = square.getSquareColour();
                //Symbols squareSymbol = square.getSquareSymbol();
                //System.out.println(x + "," + y + squareColour + squareSymbol);
            }
        }
        return board;
    }


    /**
     * A method to return a square at coordinates x, y from the board
     */
    
    //FIXME need to write a throws exception for index out of bounds
    public static Square getSquare(int x, int y, Board board){
        Square square = board.getBoard()[x][y];

        return square;
    }


    /**
     * A method to generate an ArrayList of colour constants for the board
     */
    public static ArrayList<Object> generateColours(){
        ArrayList<Object> coloursList = new ArrayList<>(List.of(Colours.values()));

        return coloursList;
    }
    /**
     * A method to generate an ArrayList of symbol constants for the board
     */
    public static ArrayList<Object> generateSymbols(){
        ArrayList<Object> symbolsList = new ArrayList<>(List.of(Symbols.values()));

        return symbolsList;
    }



}