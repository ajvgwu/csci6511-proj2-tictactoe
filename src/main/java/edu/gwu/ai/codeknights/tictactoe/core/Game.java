package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.Map;
import java.util.Objects;

import edu.gwu.ai.codeknights.tictactoe.gui.util.Player;
import org.pmw.tinylog.Logger;

public class Game {

    public static final char BLANK_SPACE_CHAR = '_';
    public static final char FIRST_PLAYER_CHAR = 'X';
    public static final char OTHER_PLAYER_CHAR = 'O';

    private final int firstPlayerId;
    private final int otherPlayerId;
    private final long id;
    private final int rowLen;
    private final int colLen;
    private final int winLength;
    private final BoardMatrix board;

    public Game(final long id, final int rowLen, final int colLen, final int winLength, final
    Integer[][] board, int masterId, int opId) {
        this.id = id;
        this.rowLen = rowLen;
        this.colLen = colLen;
        this.winLength = winLength;
        this.firstPlayerId = masterId;
        this.otherPlayerId = opId;
        this.board = new BoardMatrix(rowLen, colLen, board);
    }

    public int getRowLen() {
        return rowLen;
    }

    public int getWinLength() {
        return winLength;
    }

    public Integer getCellValue(final int rowIdx, final int colIdx) {
        return board.getCellValue(rowIdx, colIdx);
    }

    public void setCellValue(final int rowIdx, final int colIdx, final Integer value) {
        board.setCellValue(rowIdx, colIdx, value);
    }

    protected int countPlayerOrNull(final Integer player) {
        int count = 0;
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < rowLen; j++) {
                final Integer value = board.getCellValue(i, j);
                if (Objects.equals(player, value)) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countEmpty() {
        return countPlayerOrNull(null);
    }

    public int countFirstPlayer() {
        return countPlayerOrNull(firstPlayerId);
    }

    public int countOtherPlayer() {
        return countPlayerOrNull(otherPlayerId);
    }

    public int getNextPlayer() {
        return countOtherPlayer() < countFirstPlayer() ? otherPlayerId : firstPlayerId;
    }

    public int getPrevPlayer() {
        return countOtherPlayer() == countFirstPlayer() ? otherPlayerId : firstPlayerId;
    }

    protected boolean checkLineForWin(final Integer[] line, final int player) {
        if (line.length >= winLength) {
            int numInSequence = 0;
            for (int i = 0; i < line.length; i++) {
                final Integer value = line[i];
                if (value != null && value == player) {
                    numInSequence++;
                    if (numInSequence >= winLength) {
                        return true;
                    }
                } else {
                    numInSequence = 0;
                }
            }
        }
        return false;
    }

    public boolean didPlayerWin(final int player) {
        // Check all straight lines on the board
        final Map<String, Integer[]> lineMap = board.getAllLines(winLength);
        for (final String name : lineMap.keySet()) {
            final Integer[] line = lineMap.get(name);
            if (checkLineForWin(line, player)) {
                // Player won on the current line
                Logger.trace("player {} won on {}", player, name);
                return true;
            }
        }

        // Player has not won
        Logger.trace("player {} has not won", player);
        return false;
    }

    public boolean didFirstPlayerWin() {
        return didPlayerWin(firstPlayerId);
    }

    public boolean didOtherPlayerWin() {
        return didPlayerWin(otherPlayerId);
    }

    public boolean didAnyPlayerWin() {
        return didFirstPlayerWin() || didOtherPlayerWin();
    }

    public boolean isGameOver() {
        // Check if board is full
        boolean isBoardFull = true;
        for (int i = 0; i < rowLen; i++) {
            for (int j = 0; j < rowLen; j++) {
                if (board.getCellValue(i, j) == null) {
                    isBoardFull = false;
                    break;
                }
            }
            if (!isBoardFull) {
                break;
            }
        }

        // If board is not full, check if either player won
        return isBoardFull || didAnyPlayerWin();
    }

    public Map<String, Integer[]> getAllLines() {
        return board.getAllLines();
    }

    public Map<String, Move[]> getAllLinesOfMove() {
        return board.getAllLinesOfMove();
    }

    public Map<String, Move[]> getAllLinesOfMove(int winLength) {
        return board.getAllLinesOfMove(winLength);
    }

    protected String toStringAllLines(final String prefix) {
        final StringBuilder bldr = new StringBuilder();
        final Map<String, Integer[]> lineMap = board.getAllLines();
        for (final String name : lineMap.keySet()) {
            if (bldr.length() > 0) {
                bldr.append("\n");
            }
            bldr.append(prefix).append(name).append(": ");
            final Integer[] line = lineMap.get(name);
            for (Integer aLine : line) {
                bldr.append(" ").append(String.valueOf(aLine)).append(" ");
            }
        }
        return bldr.toString();
    }

    public long getBoardHash() {
        return board.getHash(getNextPlayer());
    }

    public String getBoardStatus(){
        if(!isGameOver()){
            return "IN_PROGRESS";
        }else{
            // game over
            if(!didAnyPlayerWin()){
                // no winner
                return "DRAW";
            }else{
                if(didFirstPlayerWin()){
                    return String.valueOf(firstPlayerId)+" WIN";
                }else{
                    return String.valueOf(otherPlayerId)+" WIN";
                }
            }
        }
    }

    public Game getCopy(){
        return new Game(id, rowLen, colLen, winLength, board.getAllRows(),
                firstPlayerId, otherPlayerId);
    }

    @Override
    public String toString() {
        final StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < rowLen; i++) {
            if (bldr.length() > 0) {
                bldr.append("\n");
            }
            for (int j = 0; j < rowLen; j++) {
                final Integer value = board.getCellValue(i, j);
                bldr.append(" " + (value != null
                        ? value == firstPlayerId ? FIRST_PLAYER_CHAR : value == otherPlayerId ? OTHER_PLAYER_CHAR : "?"
                        : BLANK_SPACE_CHAR) + " ");
            }
        }
        return bldr.toString();
    }

    public long getId() {
        return id;
    }

    public int getFirstPlayerId() {
        return firstPlayerId;
    }

    public int getOtherPlayerId() {
        return otherPlayerId;
    }
}
