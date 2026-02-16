package edu.uob;

import java.io.Serial;
import java.io.Serializable;

public class OXOController implements Serializable {
    @Serial private static final long serialVersionUID = 1;
    private OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if (gameModel.getWinner() != null || gameModel.isGameDrawn()) {
            return;
        }
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }
        char rowchar = command.charAt(0);
        char columnchar = command.charAt(1);
        if(!Character.isAlphabetic(rowchar)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, rowchar);
        }
        if(!Character.isDigit(columnchar)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, columnchar);
        }
        int row = Character.toLowerCase(rowchar) - 'a';
        int column = Character.getNumericValue(columnchar) - 1;
        if(row < 0 || row >= gameModel.getNumberOfRows()){
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row+1);
        }
        if(column < 0 || column >= gameModel.getNumberOfColumns()){
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, column+1);
        }
        if (gameModel.getCellOwner(row, column) != null) {
            throw new OXOMoveException.CellAlreadyTakenException(row, column);
        }
        int currentPlayerNumber = gameModel.getCurrentPlayerNumber();
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerNumber);
        gameModel.setCellOwner(row, column, currentPlayer);
        checkForWinner(row, column);
        if(gameModel.getWinner() == null && !gameModel.isGameDrawn()) {
            int nextPlayerNumber = (gameModel.getCurrentPlayerNumber() + 1) % gameModel.getNumberOfPlayers();
            gameModel.setCurrentPlayerNumber(nextPlayerNumber);
        }

    }
    public void addRow() {
        if(gameModel.getWinner() != null) return;
        gameModel.addRow();
        if (gameModel.isGameDrawn()) {
            gameModel.setGameDrawn(false);
        }
    }
    public void removeRow() {
        if(gameModel.getWinner() != null) return;
        gameModel.removeRow();
    }
    public void addColumn() {
        if(gameModel.getWinner() != null) return;
        gameModel.addColumn();
        if (gameModel.isGameDrawn()) {
            gameModel.setGameDrawn(false);
        }
    }
    public void removeColumn() {
        if(gameModel.getWinner() != null) return;
        gameModel.removeColumn();
    }
    public void increaseWinThreshold() {
        int min1 = gameModel.getNumberOfRows();
        int min2 = gameModel.getNumberOfColumns();
        int min;
        if(min1 > min2) {
            min = min2;
        }else{
            min = min1;
        }
        if(gameModel.getWinThreshold() < min){
            if(gameModel.getWinner() == null) {
                gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
            }
        }
    }
    public void decreaseWinThreshold() {
        if(gameModel.getWinThreshold() > 3){
            if(!hasGameStarted()) {
                gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
            }
        }
    }
    public void reset() {
        for(int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for(int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
        gameModel.setWinner(null);
        gameModel.setGameDrawn(false);
    }

    private void checkForWinner(int row, int col) {
        OXOPlayer player = gameModel.getCellOwner(row, col);
        int threshold = gameModel.getWinThreshold();
        if(player == null){
            return;
        }
        if(     countConsecutive(row, col, 0, 1, player) + countConsecutive(row, col, 0, -1, player) - 1 >= threshold ||
                countConsecutive(row, col, 1, 0, player) + countConsecutive(row, col, -1, 0, player) - 1 >= threshold ||
                countConsecutive(row, col, 1, 1, player) + countConsecutive(row, col, -1, -1, player) - 1 >= threshold ||
                countConsecutive(row, col, 1, -1, player) + countConsecutive(row, col, -1, 1, player) - 1 >= threshold ){
            gameModel.setWinner(player);
            return;
        }
        checkForDraw();
    }

    private int countConsecutive(int row, int col, int rowDir, int colDir, OXOPlayer player) {
        int count = 0;
        int r = row;
        int c = col;
        while(r >= 0 && r < gameModel.getNumberOfRows() && c >= 0 && c < gameModel.getNumberOfColumns() && gameModel.getCellOwner(r, c) == player) {
            count++;
            r = r +rowDir;
            c = c +colDir;
        }
        return count;
    }

    private void checkForDraw() {

        for(int r = 0; r < gameModel.getNumberOfRows(); r++){
            for(int c = 0; c < gameModel.getNumberOfColumns(); c++){
                if(gameModel.getCellOwner(r,c) == null){
                    gameModel.setGameDrawn(false);
                    return;
                }
            }
        }

        gameModel.setGameDrawn(true);
    }

    public boolean hasGameStarted() {
        boolean GameStarted = false;
        for (int r = 0; r < gameModel.getNumberOfRows(); r++) {
            for (int c = 0; c < gameModel.getNumberOfColumns(); c++) {
                if (gameModel.getCellOwner(r, c) != null) {
                    GameStarted = true;
                }
            }
        }
        return GameStarted;
    }

}
