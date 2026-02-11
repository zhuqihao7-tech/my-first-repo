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
        int row = Character.toLowerCase(rowchar) - 'a';
        int column = Character.getNumericValue(columnchar) - 1;
        if (gameModel.getCellOwner(row, column) != null) {
            return;
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
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
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

}
