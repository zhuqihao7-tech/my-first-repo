package edu.uob;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class OXOModel implements Serializable {
    @Serial private static final long serialVersionUID = 1;
    /*private OXOPlayer[][] cells;*/
    private ArrayList<ArrayList<OXOPlayer>> cells;
    /*private OXOPlayer[] players;*/
    private ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        /*cells = new OXOPlayer[numberOfRows][numberOfColumns];*/
        cells = new ArrayList<>();
        for (int i = 0; i < numberOfRows; i++) {
            ArrayList<OXOPlayer> row = new ArrayList<>();
            for (int j = 0; j < numberOfColumns; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        /*players = new OXOPlayer[2];*/
        players = new ArrayList<>();
    }

    public int getNumberOfPlayers() {
        /*return players.length;*/
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        /*for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }*/
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {
        /*return players[number];*/
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        /*return cells.length;*/
        return cells.size();
    }

    public int getNumberOfColumns() {
        /*return cells[0].length;*/
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        /*return cells[rowNumber][colNumber];*/
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        /*cells[rowNumber][colNumber] = player;*/
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) {
        gameDrawn = isDrawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addRow(){
        if(getNumberOfRows() >=9){
            return;
        }
        ArrayList<OXOPlayer> newRow = new ArrayList<>();
        for(int i = 0; i < getNumberOfColumns(); i++){
            newRow.add(null);
        }
        cells.add(newRow);
    }

    public void addColumn(){
        if(getNumberOfColumns() >=9){
            return;
        }
        for(ArrayList<OXOPlayer> row : cells){
            row.add(null);
        }
    }

    public void removeRow(){
        if(getNumberOfRows() <=3){
            return;
        }
        ArrayList<OXOPlayer> lastRow = cells.get(getNumberOfRows()-1);
        for(OXOPlayer cell : lastRow){
            if(cell != null){
                return;
            }
        }
        cells.remove(getNumberOfRows()-1);
    }

    public void removeColumn(){
        if(getNumberOfColumns() <=3){
            return;
        }
        int lastCol = getNumberOfColumns()-1;
        for(ArrayList<OXOPlayer> row : cells){
            if(row.get(lastCol) != null){
                return;
            }
        }
        for(ArrayList<OXOPlayer> row : cells){
            row.remove(lastCol);
        }
    }
}
