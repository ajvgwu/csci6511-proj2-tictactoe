package edu.gwu.ai.codeknights.tictactoe.filtering.core;

import java.util.List;
import java.util.Objects;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;

public class TicTacToeGame {

  private final int dim;
  private final int winLength;
  private final long gameId;
  private final Player player1;
  private final Player player2;

  private final Board board;

  public TicTacToeGame(final int dim, final int winLength, final long gameId, final Player player1,
    final Player player2) {
    this.dim = dim;
    this.winLength = winLength;
    this.gameId = gameId;
    this.player1 = player1;
    this.player2 = player2;

    board = new Board(dim);
  }

  public int getDim() {
    return dim;
  }

  public int getWinLength() {
    return winLength;
  }

  public long getGameId() {
    return gameId;
  }

  public Player getPlayer1() {
    return player1;
  }

  public Player getPlayer2() {
    return player2;
  }

  public Board getBoard() {
    return board;
  }

  public void populate(final String[] args) {
    final String player1Mark = String.valueOf(player1.getMarker());
    final String player2Mark = String.valueOf(player2.getMarker());
    int argIdx = 0;
    for (int rowIdx = 0; rowIdx < dim && argIdx < args.length; rowIdx++) {
      for (int colIdx = 0; colIdx < dim && argIdx < args.length; colIdx++) {
        Player player = null;
        final String arg = args[argIdx];
        if (arg != null) {
          if (arg.equals(player1Mark)) {
            player = player1;
          }
          else if (arg.equals(player2Mark)) {
            player = player2;
          }
        }
        board.getCell(rowIdx, colIdx).setPlayer(player);
        argIdx++;
      }
    }
  }

  public boolean checkValidGameState(final boolean throwExceptionIfInvalid) throws StateException {
    if (player1 == null || player2 == null) {
      if (throwExceptionIfInvalid) {
        throw new StateException("player1 and player2 must be non-null");
      }
      return false;
    }
    if (player1.getId() == player2.getId() || player1.getMarker() == player2.getMarker()) {
      if (throwExceptionIfInvalid) {
        throw new StateException("player1 and player2 must have different IDs and markers");
      }
      return false;
    }
    final int numP1 = board.countPlayer(player1);
    final int numP2 = board.countPlayer(player2);
    if (numP1 < numP2 || numP1 - numP2 > 1) {
      if (throwExceptionIfInvalid) {
        throw new StateException("player1 may have 0 or 1 moves more than player2");
      }
      return false;
    }
    return true;
  }

  public boolean isValidGameState() {
    try {
      return checkValidGameState(false);
    }
    catch (final StateException e) {
      // Should not happen
      return false;
    }
  }

  private boolean didPlayerWinOnLine(final Player player, final List<Cell> line) {
    int numConsecutive = 0;
    for (int idx = 0; idx < line.size(); idx++) {
      if (line.get(idx).isPopulatedBy(player)) {
        numConsecutive++;
        if (numConsecutive >= winLength) {
          return true;
        }
      }
      else {
        numConsecutive = 0;
      }
    }
    return false;
  }

  public boolean didPlayerWin(final Player player) {
    return board.getLinesAtLeastLength(winLength).parallelStream()
      .anyMatch(line -> didPlayerWinOnLine(player, line));
  }

  public boolean didPlayer1Win() {
    return didPlayerWin(player1);
  }

  public boolean didPlayer2Win() {
    return didPlayerWin(player2);
  }

  public boolean didAnyWin() {
    return didPlayerWin(player1) || didPlayerWin(player2);
  }

  public boolean isEarlyDraw() {
    // TODO: implement
    Logger.debug("implement TicTacToeGame.isEarlyDraw()");
    return false;
  }

  public boolean isGameOver() {
    return didAnyWin() || board.isFull() || isEarlyDraw();
  }

  public Player getNextPlayer() {
    final int p1Count = board.countPlayer(player1);
    final int p2Count = board.countPlayer(player2);
    return p1Count <= p2Count ? player1 : player2;
  }

  public Player getOtherPlayer(final Player player) {
    return Objects.equals(player, player2) ? player1 : player2;
  }

  public void playInCell(final int rowIdx, final int colIdx, final Player player) {
    board.getCell(rowIdx, colIdx).setPlayer(player);
  }

  public void playInCell(final Cell cell, final Player player) {
    playInCell(cell.getRowIdx(), cell.getColIdx(), player);
  }

  public void tryPlayNextCell() throws GameException {
    final Player nextPlayer = getNextPlayer();
    Cell cell = nextPlayer.chooseCell(this);
    if (cell == null) {
      cell = board.getEmptyCells().parallelStream().findFirst().orElse(null);
    }
    if (cell == null) {
      throw new GameException("no empty cell available player: " + String.valueOf(nextPlayer));
    }
    playInCell(cell, nextPlayer);
  }

  public List<Cell> getLongestOpenSublineForPlayer(final List<Cell> line, final Player player) {
    Integer longestStartIdx = null;
    Integer longestEndIdx = null;
    Integer curStartIdx = null;
    Integer curEndIdx = null;
    for (int idx = 0; idx < line.size(); idx++) {
      final Cell cell = line.get(idx);
      if (cell.isEmpty() || cell.isPopulatedBy(player)) {
        if (curStartIdx == null) {
          curStartIdx = idx;
        }
        curEndIdx = idx;
      }
      else {
        int curLen = 0;
        if (curStartIdx != null && curEndIdx != null) {
          curLen = curEndIdx - curStartIdx;
        }
        int longestLen = 0;
        if (longestStartIdx != null && longestEndIdx != null) {
          longestLen = longestEndIdx - longestStartIdx;
        }
        if (curLen > longestLen) {
          longestStartIdx = curStartIdx;
          longestEndIdx = curEndIdx;
        }
        curStartIdx = null;
        curEndIdx = null;
      }
    }
    final int startIdx = longestStartIdx != null ? longestStartIdx : 0;
    final int endIdx = Math.max(startIdx, longestEndIdx != null ? longestEndIdx : 0);
    return line.subList(startIdx, endIdx);
  }

  public long evaluatePlayerUtility(final Player player) {
    long score = 0L;
    final Player opponent = getOtherPlayer(player);
    for (final List<Cell> line : board.getLinesAtLeastLength(winLength)) {
      if (didPlayerWinOnLine(player, line)) {
        score += 2 * dim * dim * dim;
      }
      else if (didPlayerWinOnLine(opponent, line)) {
        score -= 2 * dim * dim * dim;
      }
      final List<Cell> longestSeq = getLongestOpenSublineForPlayer(line, player);
      score += longestSeq.parallelStream()
        .filter(cell -> cell.isPopulatedBy(player))
        .mapToInt(cell -> 1)
        .sum();
      final List<Cell> longestOppSeq = getLongestOpenSublineForPlayer(line, opponent);
      score -= longestOppSeq.parallelStream()
        .filter(cell -> cell.isPopulatedBy(opponent))
        .mapToInt(cell -> 1)
        .sum();
    }
    return score;
  }

  public long evaluatePlayer1Utility() {
    return evaluatePlayerUtility(player1);
  }

  public long evaluatePlayer2Utility() {
    return evaluatePlayerUtility(player2);
  }

  public TicTacToeGame getCopy(final long newGameId, final Player newPlayer1, final Player newPlayer2) {
    final TicTacToeGame copy = new TicTacToeGame(dim, winLength, newGameId, newPlayer1, newPlayer2);
    for (final Cell oldCell : board.getAllCells()) {
      final Cell newCell = copy.getBoard().getCell(oldCell.getRowIdx(), oldCell.getColIdx());
      if (oldCell.isPopulatedBy(player1)) {
        newCell.setPlayer(newPlayer1);
      }
      else if (oldCell.isPopulatedBy(player2)) {
        newCell.setPlayer(newPlayer2);
      }
    }
    return copy;
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append("dim=").append(dim)
      .append(", winLength=").append(winLength)
      .append(", gameId=").append(gameId)
      .append(", player1=").append(player1).append("(utility=").append(evaluatePlayer1Utility()).append(")")
      .append(", player2=").append(player2).append("(utility=").append(evaluatePlayer2Utility()).append(")")
      .append(", isValidGameState=").append(isValidGameState())
      .append("\n")
      .append(board)
      .toString();
  }
}
