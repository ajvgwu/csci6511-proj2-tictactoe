package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;

public class Game {

  private final int dim;
  private final int winLength;
  private final long gameId;
  private final Player player1;
  private final Player player2;

  private final Board board;

  public Game(final int dim, final int winLength, final long gameId, final Player player1, final Player player2) {
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

  public boolean didPlayerWinOnLine(final Player player, final List<Cell> line) {
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
    return board.getLinesAtLeastLength(winLength).stream()
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

  public boolean isGameOver() {
    return didAnyWin() || board.isFull();
  }

  public String getBoardStatus() {
    if (!isGameOver()) {
      return "IN_PROGRESS";
    }
    else {
      // game over
      if (!didAnyWin()) {
        // no winner
        return "DRAW";
      }
      else if (didPlayer1Win()) {
        return String.valueOf(player1) + " WIN";
      }
      else {
        return String.valueOf(player2) + " WIN";
      }
    }
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
    if (cell != null) {
      Logger.debug("player {} chose cell at ({},{})", nextPlayer, cell.getRowIdx(), cell.getColIdx());
    }
    else {
      Logger.debug("player {} did not choose a cell, trying to find empty cell", nextPlayer);
      cell = board.getEmptyCells().stream()
        .findAny()
        .orElse(null);
    }
    if (cell != null) {
      playInCell(cell, nextPlayer);
    }
    else {
      throw new GameException("no empty cell available for player: " + String.valueOf(nextPlayer));
    }
  }

  /**
   * Tries to parse the given {@code coords} string and return the corresponding {@code Cell} from the board.
   * The expected format is "rowIdx,colIdx" (zero-based). For example, "0,0" is the top-left cell.
   * @param coords the coordinate of the cell in format "rowIdx,colIdx"
   * @return the {@code Cell} at the given coord if the string is valid and it is within bounds, otherwise {@code null}
   */
  public Cell tryGetCellFromCoord(final String coords) {
    if (coords != null) {
      final String[] parts = coords.split(",", 2);
      if (parts != null && parts.length >= 2) {
        final String rowVal = parts[0];
        final String colVal = parts[1];
        try {
          final int rowIdx = Integer.parseInt(rowVal.trim());
          final int colIdx = Integer.parseInt(colVal.trim());
          if (rowIdx < dim && colIdx < dim) {
            return board.getCell(rowIdx, colIdx);
          }
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse cell coordinates (expected format \"rowIdx,colIdx\"): " + coords);
        }
      }
    }
    return null;
  }

  /**
   * TODO: currently, this is worst case an O(N^2) operation... can it be improved ???
   *
   * For the given line of cells, find the longest continuous subsequence of cells that are either empty or populated
   * by the player, as long as at least one cell in that subsequence is empty. If multiple subsequences have the same
   * length, choose the one with the most cells already populated by the player.
   * @param line the line to search
   * @param player the player
   * @return the longest subsequence, or {@code null} if no satisfiable subsequence was found
   */
  public List<Cell> getLongestOpenSublineForPlayer(final List<Cell> line, final Player player) {
    if (line.stream().noneMatch(cell -> cell.isPopulatedBy(player) || cell.isEmpty())) {
      return Collections.emptyList();
    }
    int longestLen = 0;
    final List<List<Cell>> candidateSublines = new ArrayList<>();
    for (int startIdx = 0; startIdx < line.size(); startIdx++) {
      for (int endIdx = startIdx + 1; endIdx <= line.size(); endIdx++) {
        final List<Cell> curSubline = line.subList(startIdx, endIdx);
        if (curSubline.size() >= longestLen && curSubline.stream().anyMatch(cell -> cell.isPopulatedBy(player))) {
          boolean hasAtLeastOneEmpty = false;
          boolean isAllPlayerOrEmpty = true;
          for (final Cell cell : curSubline) {
            if (cell.isEmpty()) {
              hasAtLeastOneEmpty = true;
            }
            else if (!cell.isPopulatedBy(player)) {
              isAllPlayerOrEmpty = false;
              break;
            }
          }
          if (hasAtLeastOneEmpty && isAllPlayerOrEmpty) {
            final int curLen = curSubline.size();
            if (curLen >= longestLen) {
              if (curLen > longestLen) {
                candidateSublines.clear();
              }
              longestLen = curLen;
              candidateSublines.add(curSubline);
            }
          }
        }
      }
    }
    int mostPopulated = 0;
    List<Cell> bestLine = Collections.emptyList();
    for (final List<Cell> subline : candidateSublines) {
      final int numPopulated = subline.stream().mapToInt(cell -> cell.isPopulatedBy(player) ? 1 : 0).sum();
      if (numPopulated > mostPopulated) {
        mostPopulated = numPopulated;
        bestLine = subline;
      }
    }
    return bestLine;
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
      if (longestSeq.size() >= winLength) {
        final int numPopulated = longestSeq.stream()
          .filter(cell -> cell.isPopulatedBy(player))
          .mapToInt(cell -> 1)
          .sum();
        score += numPopulated * numPopulated;
      }
      final List<Cell> longestOppSeq = getLongestOpenSublineForPlayer(line, opponent);
      if (longestOppSeq.size() >= winLength) {
        final int numPopulated = longestOppSeq.stream()
          .filter(cell -> cell.isPopulatedBy(opponent))
          .mapToInt(cell -> 1)
          .sum();
        score -= numPopulated * numPopulated;
      }
    }
    return score;
  }

  public long evaluatePlayer1Utility() {
    return evaluatePlayerUtility(player1);
  }

  public long evaluatePlayer2Utility() {
    return evaluatePlayerUtility(player2);
  }

  public String getBoardHash() {
    return board.getHash();
  }

  public Game getCopy(final long newGameId, final Player newPlayer1, final Player newPlayer2) {
    final Game copy = new Game(dim, winLength, newGameId, newPlayer1, newPlayer2);
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

  public Game getCopy() {
    return getCopy(gameId, player1, player2);
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
