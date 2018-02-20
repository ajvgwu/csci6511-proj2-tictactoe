package edu.gwu.ai.codeknights.tictactoe;

import java.util.Map;
import java.util.Objects;

import org.pmw.tinylog.Logger;

public class Game {

  public static final int MAX_DIM = 20;
  public static final int MAX_WIN_LENGTH = 8;
  public static final char BLANK_SPACE_CHAR = '_';
  public static final int FIRST_PLAYER_VALUE = 10;
  public static final char FIRST_PLAYER_CHAR = 'X';
  public static final int OTHER_PLAYER_VALUE = 20;
  public static final char OTHER_PLAYER_CHAR = 'O';

  private final int dim;
  private final int winLength;
  private final BoardMatrix board;

  public Game(final int dim, final int winLength, final Integer[][] board) throws DimensionException, StateException {
    // Check bounds according to: https://ai2018spring.slack.com/archives/C8LB24170/p1518457851000533
    if (dim > MAX_DIM) {
      throw new DimensionException(String.format("dim=%d, should be <= %d", dim, MAX_DIM));
    }
    this.dim = dim;

    // Check bounds according to: https://ai2018spring.slack.com/archives/C8LB24170/p1518457851000533
    if (winLength > MAX_WIN_LENGTH) {
      throw new DimensionException(String.format("winLength=%d, should be <= %d", winLength, MAX_WIN_LENGTH));
    }
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
          else {
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

  public Game(final int dim, final int winLength) throws DimensionException, StateException {
    this(dim, winLength, new Integer[dim][dim]);
  }

  public Game(final int dim) throws DimensionException, StateException {
    this(dim, dim);
  }

  public Game(final int winLength, final Integer[][] board) throws DimensionException, StateException {
    this(board.length, winLength, board);
  }

  public Game(final Integer[][] board) throws DimensionException, StateException {
    this(board.length, board.length, board);
  }

  public Game() throws DimensionException, StateException {
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
  }

  protected int countPlayerOrNull(final Integer player) {
    int count = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        final Integer value = board.getCellValue(i, j);
        if (value != null && value.equals(player)) {
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

  public int getNextPlayer() {
    return countOtherPlayer() < countFirstPlayer() ? Game.OTHER_PLAYER_VALUE : Game.FIRST_PLAYER_VALUE;
  }

  public int getPrevPlayer() {
    return countOtherPlayer() == countFirstPlayer() ? Game.OTHER_PLAYER_VALUE : Game.FIRST_PLAYER_VALUE;
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

    // If board is not full, check if either player won
    return isBoardFull || didAnyPlayerWin();
  }

  public Map<String, Integer[]> getAllLines(){
    return board.getAllLines();
  }

  public Map<String, Game.Move[]> getAllLinesOfMove(){
    return board.getAllLinesOfMove();
  }

  public Map<String, Game.Move[]> getAllLinesOfMove(int winLength){
    return board.getAllLinesOfMove(winLength);
  }


  /*
  public int getScore(final int player) {
    // TODO: fix this ???
    int score = 0;
    final Collection<Integer[]> lines = board.getAllLines(winLength).values();
    for (final Integer[] line : lines) {
      int maxConsecutivePlayer = 0;
      int maxConsecutivePlayerOrEmpty = 0;
      int maxConsecutiveOther = 0;
      int maxConsecutiveOtherOrEmpty = 0;
      int numConsecutivePlayer = 0;
      int numConsecutivePlayerOrEmpty = 0;
      int numConsecutiveOther = 0;
      int numConsecutiveOtherOrEmpty = 0;
      for (final Integer value : line) {
        final boolean isEmpty = value == null;
        final boolean isPlayer = !isEmpty && value == player;
        final boolean isOther = !isEmpty && !isPlayer;
        if (isEmpty) {
          numConsecutivePlayer = 0;
          numConsecutivePlayerOrEmpty++;
          numConsecutiveOther = 0;
          numConsecutiveOtherOrEmpty++;
        }
        else if (isPlayer) {
          numConsecutivePlayer++;
          numConsecutivePlayerOrEmpty = 0;
          numConsecutiveOther = 0;
          numConsecutiveOtherOrEmpty = 0;
        }
        else if (isOther) {
          numConsecutivePlayer = 0;
          numConsecutivePlayerOrEmpty = 0;
          numConsecutiveOther++;
          numConsecutiveOtherOrEmpty = 0;
        }
        maxConsecutivePlayer = Math.max(maxConsecutivePlayer, numConsecutivePlayer);
        maxConsecutivePlayerOrEmpty = Math.max(maxConsecutivePlayerOrEmpty, numConsecutivePlayerOrEmpty);
        maxConsecutiveOther = Math.max(maxConsecutiveOther, numConsecutiveOther);
        maxConsecutiveOtherOrEmpty = Math.max(maxConsecutiveOtherOrEmpty, numConsecutiveOtherOrEmpty);
      }
      if (maxConsecutivePlayer >= winLength) {
        return Integer.MAX_VALUE;
        // score += dim * dim * dim * maxConsecutivePlayer;
      }
      else if (maxConsecutiveOther >= winLength) {
        return Integer.MIN_VALUE;
        // score -= dim * dim * dim * maxConsecutiveOther;
      }
      else if (maxConsecutivePlayerOrEmpty >= winLength) {
        score += maxConsecutivePlayerOrEmpty * (maxConsecutivePlayer + 1);
      }
    }
    return score;
  }
  */

  protected String toStringAllLines(final String prefix) {
    final StringBuilder bldr = new StringBuilder();
    final Map<String, Integer[]> lineMap = board.getAllLines();
    for (final String name : lineMap.keySet()) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      bldr.append(prefix + name + ": ");
      final Integer[] line = lineMap.get(name);
      for (int i = 0; i < line.length; i++) {
        bldr.append(" " + String.valueOf(line[i]) + " ");
      }
    }
    return bldr.toString();
  }

  public long getBoardHash() {
    return board.getHash(getNextPlayer());
  }

  public Game getCopy() throws DimensionException, StateException {
    return new Game(dim, winLength, board.getAllRows());
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
    return bldr.toString();
  }

  public static class Move {

    public final int rowIdx;
    public final int colIdx;
    public final Integer player;

    private Long score;

    public Move(final int rowIdx, final int colIdx, final Integer player, final
    Long score) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.player = player;

      this.score = score;
    }

    public Long getScore() {
      return score;
    }

    public void setScore(final Long score) {
      this.score = score;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Move move = (Move) o;
      return rowIdx == move.rowIdx &&
              colIdx == move.colIdx &&
              Objects.equals(player, move.player) &&
              Objects.equals(score, move.score);
    }

    @Override
    public int hashCode() {
      return Objects.hash(rowIdx, colIdx, player, score);
    }

    @Override
    public String toString() {
      return String.format("player %d at (%d,%d) scores %d", player, rowIdx, colIdx, score);
    }
  }
}
