package minigames.server.telepathy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A class representing a Telepathy board, includes a method to access Tile objects from a board.
 */

public class Board{

    private Tile[][] board;
    
    private int ROWS = 9; // adjustable variable for grid size
    private int COLS = 9; // adjustable variable for grid size

    /**
     * board constructor
     */
    public Board(){
        this.board = generateBoard(ROWS, COLS);
    }

    /**
     * A method to generate a 2D array of tile objects with assigned colour and symbol.
     * 
     * @param rows an int representing number of rows on the board
     * @param cols an int representing number of columns on the board
     * @return a 2D array of Tile objects
     */
    public Tile[][] generateBoard(int rows, int cols) {
        Tile[][] board = new Tile[rows][cols];
        Random randNumber = new Random(16); // every board will be the same because of 'seed' number in formula
        ArrayList<Object> colourList = generateColours();
        ArrayList<Object> symbolList = generateSymbols();
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board.length; col++) {
                Colours colour = (Colours) colourList.get(randNumber.nextInt(9));
                Symbols symbol = (Symbols) symbolList.get(randNumber.nextInt(9));
                Tile tile = new Tile(row, col, colour, symbol);
                board[row][col] = tile;
                //redundant code: used to quickly check random allocation was functioning
                //int x = tile.getHorizontalPos();
                //int y = tile.getVerticalPos();
                //Colours tileColour = tile.getTileColour();
                //Symbols tileSymbol = tile.getTileSymbol();
                //System.out.println(x + "," + y + tileColour + tileSymbol);
            }
        }
        return board;
    }

    
    /**
     * Get the board of tiles
     * @return A 2-dimensional array of Tiles that represents the Telepathy game board.
     */
    public Tile[][] getBoard() {
        return this.board;
    }

    /**
     * Get a Tile at a specific coordinate of this Board.
     * @param row The row of the desired Tile.
     * @param column The column of the desired Tile.
     * @return The Tile object at the desired coodinate.
     */
    public Tile getTile(int row, int column) {
        return this.board[row][column];
    }

    /**
     * A method to return a square at coordinates x, y from the board
     */
    public Tile getTile(int x, int y, Board board){

        Tile tile = board.getBoard()[x][y];

        return tile;
    }


    /**
     * A method to generate an ArrayList of colour constants for the board
     */
    public ArrayList<Object> generateColours() {
        ArrayList<Object> coloursList = new ArrayList<>(List.of(Colours.values()));

        return coloursList;
    }
    
    /**
     * A method to generate an ArrayList of symbol constants for the board
     */
    public ArrayList<Object> generateSymbols(){
        ArrayList<Object> symbolsList = new ArrayList<>(List.of(Symbols.values()));

        return symbolsList;
    }



}