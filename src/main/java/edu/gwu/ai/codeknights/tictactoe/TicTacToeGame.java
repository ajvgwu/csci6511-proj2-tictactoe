package edu.gwu.ai.codeknights.tictactoe;

import java.util.Map;

import org.pmw.tinylog.Logger;

public class TicTacToeGame {

  public static final int MAX_DIM = 20;
  public static final int MAX_WIN_LENGTH = 8;
  public static final char BLANK_SPACE_CHAR = '_';
  public static final int FIRST_PLAYER_VALUE = 1;
  public static final char FIRST_PLAYER_CHAR = 'X';
  public static final int OTHER_PLAYER_VALUE = 0;
  public static final char OTHER_PLAYER_CHAR = 'O';

  private final int dim;
  private final int winLength;
  private final BoardMatrix board;

  public TicTacToeGame(final int dim, final int winLength, final Integer[][] board)
    throws DimensionException, StateException {
    // Check bounds according to: https://ai2018spring.slack.com/archives/C8LB24170/p1518457851000533
    if (dim > MAX_DIM) {
      throw new DimensionException(String.format("dim=%d, should be <= %d", dim, MAX_DIM));
    }
    this.dim = dim;

    // Check bounds according to: https://ai2018spring.slack.com/archives/C8LB24170/p1518457851000533
    if (winLength > MAX_WIN_LENGTH) {
      throw new DimensionException(String.format("winLength=%d, should be <= %d", winLength, MAX_WIN_LENGTH));
    }
    // TODO: do we need to check that winLength <= dim ? or is it fine that players will always lose?
    this.winLength = winLength;

    // Check board size and values
    if (board.length != dim) {
      throw new DimensionException(String.format("board has wrong number of rows: %d (dim=%d)", board.length, dim));
    }
    int numFirstPlayer = 0;
    int numOtherPlayer = 0;
    for (int i = 0; i < dim; i++) {
      final Integer[] row = board[i];
      if (row.length != dim) {
        throw new DimensionException(String.format("board has wrong number of columns: %d (dim=%d)", row.length, dim));
      }
      for (int j = 0; j < dim; j++) {
        final Integer value = row[j];
        if (value != null) {
          if (value != FIRST_PLAYER_VALUE && value != OTHER_PLAYER_VALUE) {
            throw new StateException(String.format("illegal value on board at position (%d,%d): %d", i, j, value));
          }
          if (value == FIRST_PLAYER_VALUE) {
            numFirstPlayer++;
          }
          else if (value == OTHER_PLAYER_VALUE) {
            numOtherPlayer++;
          }
        }
      }
    }
    if (numOtherPlayer > numFirstPlayer || Math.abs(numFirstPlayer - numOtherPlayer) > 1) {
      throw new StateException(String.format(
        "illegal state, player %d goes first then alternates with player %d (numFirstPlayer=%d, numOtherPlayer=%d)",
        FIRST_PLAYER_VALUE, OTHER_PLAYER_VALUE, numFirstPlayer, numOtherPlayer));
    }
    this.board = new BoardMatrix(dim, board);
  }

  public TicTacToeGame(final int dim, final int winLength) throws DimensionException, StateException {
    this(dim, winLength, new Integer[dim][dim]);
  }

  public TicTacToeGame(final int dim) throws DimensionException, StateException {
    this(dim, dim);
  }

  public TicTacToeGame(final int winLength, final Integer[][] board) throws DimensionException, StateException {
    this(board.length, winLength, board);
  }

  public TicTacToeGame(final Integer[][] board) throws DimensionException, StateException {
    this(board.length, board.length, board);
  }

  public TicTacToeGame() throws DimensionException, StateException {
    this(3, 3, new Integer[3][3]);
  }

  public int getDim() {
    return dim;
  }

  public int getWinLength() {
    return winLength;
  }

  public Integer getCellValue(final int rowIdx, final int colIdx) {
    return board.getCellValue(rowIdx, colIdx);
  }

  public void setCellValue(final int rowIdx, final int colIdx, final Integer value) {
    board.setCellValue(rowIdx, colIdx, value);
    // TODO: is it worthwhile to do any consistency checking here? make sure players are alternating and counts are correct?
  }

  protected int countPlayerOrNull(final Integer player) {
    int count = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        final Integer value = board.getCellValue(i, j);
        if (value == player) {
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
    return countPlayerOrNull(FIRST_PLAYER_VALUE);
  }

  public int countOtherPlayer() {
    return countPlayerOrNull(OTHER_PLAYER_VALUE);
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
        }
        else {
          numInSequence = 0;
        }
      }
    }
    return false;
  }

  protected boolean didPlayerWin(final int player) {
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
    return didPlayerWin(FIRST_PLAYER_VALUE);
  }

  public boolean didOtherPlayerWin() {
    return didPlayerWin(OTHER_PLAYER_VALUE);
  }

  public boolean didAnyPlayerWin() {
    return didFirstPlayerWin() || didOtherPlayerWin();
  }

  public boolean isGameOver() {
    // Check if board is full
    boolean isBoardFull = true;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (board.getCellValue(i, j) == null) {
          isBoardFull = false;
          break;
        }
      }
      if (!isBoardFull) {
        break;
      }
    }

    // TODO: could also check for "early draw" condition, even if board is not full

    // If board is not full, check if either player won
    return isBoardFull || didAnyPlayerWin();
  }

  protected void toStringAllLines(final StringBuilder bldr) {
    final Map<String, Integer[]> lineMap = board.getAllLines();
    for (final String name : lineMap.keySet()) {
      bldr.append(name + ": ");
      final Integer[] line = lineMap.get(name);
      for (int i = 0; i < line.length; i++) {
        bldr.append(" " + String.valueOf(line[i]) + " ");
      }
      bldr.append("\n");
    }
  }

  @Override
  public String toString() {
    final StringBuilder bldr = new StringBuilder();
    for (int i = 0; i < dim; i++) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      for (int j = 0; j < dim; j++) {
        final Integer value = board.getCellValue(i, j);
        bldr.append(" " + (value != null
          ? value == FIRST_PLAYER_VALUE ? FIRST_PLAYER_CHAR : value == OTHER_PLAYER_VALUE ? OTHER_PLAYER_CHAR : "?"
          : BLANK_SPACE_CHAR) + " ");
      }
    }

    // TODO: FIXME: in final version, don't need to output all lines
    bldr.append("\n\n");
    toStringAllLines(bldr);

    return bldr.toString();
  }

  public static class BoardArrayHelper {

    private final Integer[][] rowMajorMatrix;
    private final Integer[][] colMajorMatrix;

    public BoardArrayHelper(final int dim, final Integer[][] rowMajorArr) {
      rowMajorMatrix = new Integer[dim][dim];
      colMajorMatrix = new Integer[dim][dim];

      // Populate
      for (int i = 0; i < dim; i++) {
        for (int j = 0; j < dim; j++) {
          rowMajorMatrix[i][j] = rowMajorArr[i][j];
          colMajorMatrix[i][j] = rowMajorArr[j][i];
        }
      }
    }
  }
}
