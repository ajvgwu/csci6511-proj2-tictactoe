package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;

/**
 * Maintains the state for a game of Tic Tac Toe played by two {@link Player}s on a square {@link Board}. Provides a
 * variety of operations to check and manipulate the game's board.
 *
 * @author ajv
 */
public class Game {

  private final int dim;
  private final int winLength;
  private final long gameId;
  private final Player player1;
  private final Player player2;

  private final Board board;

  /**
   * Construct a new game with the given board dimension, length required to win, game ID, and players.
   *
   * @param dim       the board dimension (number of rows and columns)
   * @param winLength the length required to win
   * @param gameId    the game ID
   * @param player1   the first player
   * @param player2   the second player
   */
  public Game(final int dim, final int winLength, final long gameId, final Player player1, final Player player2) {
    this.dim = dim;
    this.winLength = winLength;
    this.gameId = gameId;
    this.player1 = player1;
    this.player2 = player2;

    board = new Board(dim);
  }

  /**
   * Get the dimension of the square board (number of rows and columns).
   *
   * @return the board dimension
   */
  public int getDim() {
    return dim;
  }

  /**
   * Get the length (number of contiguous cells populated by a player in a straight line) required to win.
   *
   * @return the length required to win
   */
  public int getWinLength() {
    return winLength;
  }

  /**
   * Get the game ID.
   *
   * @return the game ID
   */
  public long getGameId() {
    return gameId;
  }

  /**
   * Get the first player.
   *
   * @return the first player
   */
  public Player getPlayer1() {
    return player1;
  }

  /**
   * Get the second player.
   *
   * @return the second player
   */
  public Player getPlayer2() {
    return player2;
  }

  /**
   * Get the board on which the game is being played.
   *
   * @return the board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Populate the board state from the given array of player markers and/or blank cells. A player marker is a single
   * character matching {@link Player#getMarker()} for {@link #player1} or {@link #player2}. Any other string is
   * interpreted as an empty cell. The board is populated starting from cell {@code (0,0)} (top-left corner), moving
   * left-to-right along each row and then top-to-bottom down the board until either (a) the last cell at
   * {@code (dim-1,dim-1)} is populated, or (b) the end of the input array is reached.
   *
   * @param args an array of strings representing player markers or blank cells
   */
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

  /**
   * Check whether the game is currently in a valid state, and optionally throw a {@link StateException} if a validity
   * condition has been violated. The game is valid if it has two players with different IDs and markers and if
   * {@link #player1} has played either the same number of moves or one more move than {@link #player2}.
   *
   * @param throwExceptionIfInvalid if {@code true}, throws a {@link StateException} if the game state is invalid
   *
   * @return {@code true} if the game state is valid, {@code false} if the game state is invalid and
   *         {@code throwExceptionIfInvalid} is false
   *
   * @throws StateException if the game state is invalid and {@code throwExceptionIfInvalid} is true
   */
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

  /**
   * Delegates to {@link #checkValidGameState(boolean)} to check whether the game is currently in a valid state, but
   * will not throw an exception if the game state is invalid.
   *
   * @return {@code true} if the game state is valid, {@code false} otherwise
   */
  public boolean isValidGameState() {
    try {
      return checkValidGameState(false);
    }
    catch (final StateException e) {
      // Should not happen
      return false;
    }
  }

  /**
   * Check whether the given player won on the given line of cells.
   *
   * @param player the player for whom a win will be checked
   * @param line   the line of cells that will be checked
   *
   * @return {@code true} if the player has populated at least {@link #winLength} contiguous cells in this line,
   *         {@code false} otherwise
   */
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

  /**
   * Check whether the given player has won anywhere on the board (populated at least {@link #winLength} contiguous
   * cells in any straight line).
   *
   * @param player the player for whom a win will be checked
   *
   * @return {@code true} if the player has won, {@code false} otherwise
   */
  public boolean didPlayerWin(final Player player) {
    return board.getLinesAtLeastLength(winLength).stream()
      .anyMatch(line -> didPlayerWinOnLine(player, line));
  }

  /**
   * Delegates to {@link #didPlayerWin(Player)} to check whether {@link #player1} has won anywhere on the board.
   *
   * @return {@code true} if {@link #player1} has won, {@code false} otherwise
   */
  public boolean didPlayer1Win() {
    return didPlayerWin(player1);
  }

  /**
   * Delegates to {@link #didPlayerWin(Player)} to check whether {@link #player2} has won anywhere on the board.
   *
   * @return {@code true} if {@link #player2} has won, {@code false} otherwise
   */
  public boolean didPlayer2Win() {
    return didPlayerWin(player2);
  }

  /**
   * Delegates to {@link #didPlayerWin(Player)} to check whether either {@link #player1} or {@link #player2} has won
   * anywhere on the board.
   *
   * @return {@code true} if either {@link #player1} or {@link #player2} has won, {@code false} otherwise
   */
  public boolean didAnyWin() {
    return didPlayerWin(player1) || didPlayerWin(player2);
  }

  /**
   * Find the cells the player could populate on the current board state that would lead to a win.
   *
   * NOTE: will return an empty set if the given player already won.
   *
   * NOTE: will make changes to the board; for thread-safe operation, synchronize on {@link #getBoard()}.
   *
   * @param player the player for whom winning cells will be found
   *
   * @return a set of empty cells that, if populated, would make this player win
   */
  public Set<Cell> getWinningCells(final Player player) {
    final Set<Cell> winners = new HashSet<>();
    if (!didPlayerWin(player)) {
      for (final Cell cell : board.getEmptyCells()) {
        if (cell.getPlayer() == null) {
          boolean didWin = false;
          synchronized (board) {
            cell.setPlayer(player);
            didWin = didPlayerWin(player);
            cell.setPlayer(null);
          }
          if (didWin) {
            winners.add(cell);
          }
        }
      }
    }
    return winners;
  }

  /**
   * Check terminal conditions to determine whether the game is over. The game is over if either player won or the
   * board is full.
   *
   * @return {@code true} if the game is over, {@code false} otherwise
   */
  public boolean isGameOver() {
    return didAnyWin() || board.isFull();
  }

  /**
   * Get a string that describes the current status of the game: either a player has won, or the game is a draw, or the
   * game is still in progress.
   *
   * @return a string describing the current status of the game
   */
  public String getGameStatus() {
    if (didPlayer1Win()) {
      return String.valueOf(player1) + " WIN";
    }
    else if (didPlayer2Win()) {
      return String.valueOf(player2) + " WIN";
    }
    else if (isGameOver()) {
      return "DRAW";
    }
    else {
      return "IN_PROGRESS";
    }
  }

  /**
   * Fetch the next player that should play on the board. For any valid game state (see
   * {@link #checkValidGameState(boolean)}), this will be {@link #player1} if the current move counts are equal, and
   * {@link #player2} otherwise. For an invalid game state, it is possible that a player must play multiple moves in a
   * row in order to catch up to the other player.
   *
   * @return the next player that should play
   */
  public Player getNextPlayer() {
    final int p1Count = board.countPlayer(player1);
    final int p2Count = board.countPlayer(player2);
    return p1Count <= p2Count ? player1 : player2;
  }

  /**
   * Fetch the opponent of the given player. If the given player is neither {@link #player1} nor {@link #player2}, the
   * behavior is undefined (but will most likely return {@code null}.
   *
   * @param player the player whose opponent should be fetched
   *
   * @return the opponent of the given player, or possibly {@code null} if the given player is not part of this game
   */
  public Player getOtherPlayer(final Player player) {
    return Objects.equals(player, player2) ? player1 : Objects.equals(player, player1) ? player2 : null;
  }

  /**
   * Convenience function that delegates to {@link Board#getCell(int, int)} and {@link Cell#setPlayer(Player)} to
   * populate the cell at {@code (rowIdx,colIdx)} by the given player.
   *
   * NOTE: no checking is performed to ensure that the cell is empty before the player populates it.
   *
   * @param rowIdx zero-based index of the row of the cell
   * @param colIdx zero-based index of the column of the cell
   * @param player the player who will populate the cell
   */
  public void playInCell(final int rowIdx, final int colIdx, final Player player) {
    board.getCell(rowIdx, colIdx).setPlayer(player);
  }

  /**
   * Delegates to {@link #playInCell(int, int, Player)} to populate a cell by the given player.
   *
   * NOTE: no checking is performed to ensure that the cell is empty before the player populates it.
   *
   * NOTE: the cell is fetched from the game board using {@link Cell#getRowIdx()} and {@link Cell#getColIdx()}; thus,
   *       there is no explicit guarantee that the cell passed to this function will actually be the one updated.
   *
   * @param cell   the cell which gives the row and column that the player will populate
   * @param player the player who will populate the cell at the given position
   */
  public void playInCell(final Cell cell, final Player player) {
    playInCell(cell.getRowIdx(), cell.getColIdx(), player);
  }

  /**
   * Makes a best-effort attempt to fetch the next player ({@link #getNextPlayer()} and play in the cell chosen by that
   * player ({@link Player#chooseCell(Game)}).
   *
   * @throws GameException if the player chooses a cell that is already populated
   * @throws GameException if no empty cell is available
   */
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
      if (cell.isPopulated()) {
        throw new GameException("cell is already populated by: " + String.valueOf(cell.getPlayer()));
      }
      playInCell(cell, nextPlayer);
    }
    else {
      throw new GameException("no empty cell available for player: " + String.valueOf(nextPlayer));
    }
  }

  /**
   * Tries to parse the given {@code coords} string and return the corresponding {@link Cell} from the board.
   * The expected format is {@code rowIdx,colIdx} (zero-based). For example, {@code 0,0} is the top-left cell.
   *
   * @param coords the coordinate of the cell
   *
   * @return the {@link Cell} at the given coord if the string is valid and it is within bounds, otherwise {@code null}
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
   *
   * @param line   the line to search
   * @param player the player
   *
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

  /**
   * For the current game state, compute a numeric value describing how advantageous the current position is for the
   * given player. Positive values are advantageous, while negative ones are not. Takes into account whether the game
   * is won (big plus) or lost (big minus); the number of cells occupied by the player in any line with win potential
   * (plus, scaled by number of cells already occupied by the player); and the same for lines with loss potential
   * (minus, scaled by number of cells occupied by opponent).
   *
   * @param player evaluate the board from the perspective of this player
   *
   * @return a numeric value describing the utility of the current game state for the given player
   */
  public long evaluatePlayerUtility(final Player player) {
    long score = 0L;
    final Player opponent = getOtherPlayer(player);
    final Player nextPlayer = getNextPlayer();

    // Consider all lines that could be involved in a win/loss
    for (final List<Cell> line : board.getLinesAtLeastLength(winLength)) {

      // Big plus for wins
      if (didPlayerWinOnLine(player, line)) {
        score += dim * dim * dim * dim;
      }

      // Big minus for losses
      else if (didPlayerWinOnLine(opponent, line)) {
        score -= dim * dim * dim * dim;
      }

      // Find the player's longest subline in this line (all player tokens or empty)
      final List<Cell> longestSeq = getLongestOpenSublineForPlayer(line, player);
      if (longestSeq.size() >= winLength) {
        // The player could win on this line, add to the score
        final int numPopulated = longestSeq.stream()
          .filter(cell -> cell.isPopulatedBy(player))
          .mapToInt(cell -> 1)
          .sum();
        score += numPopulated * numPopulated;
      }

      // Find the opponent's longest subline in this line
      final List<Cell> longestOppSeq = getLongestOpenSublineForPlayer(line, opponent);
      if (longestOppSeq.size() >= winLength) {
        // The player could lose on this line, subtract from the score
        final int numPopulated = longestOppSeq.stream()
          .filter(cell -> cell.isPopulatedBy(opponent))
          .mapToInt(cell -> 1)
          .sum();
        score -= numPopulated * numPopulated;
      }
    }

    // Depending on whose turn it is, count the number of ways the player could win/lose
    // (winning in two ways is good, but losing in two ways is bad)
    if (player.equals(nextPlayer)) {
      final Set<Cell> winners = getWinningCells(player);
      if (winners.size() > 1) {
        score += winners.size() * dim * dim;
      }
    }
    else {
      final Set<Cell> losers = getWinningCells(opponent);
      if (losers.size() > 1) {
        score -= losers.size() * dim * dim;
      }
    }

    // Return final score
    return score;
  }

  /**
   * Delegates to {@link #evaluatePlayerUtility(Player)} to compute the utility for {@link #player1}.
   *
   * @return a numeric value describing the utility of the current game state for {@link #player1}
   */
  public long evaluatePlayer1Utility() {
    return evaluatePlayerUtility(player1);
  }

  /**
   * Delegates to {@link #evaluatePlayerUtility(Player)} to compute the utility for {@link #player2}.
   *
   * @return a numeric value describing the utility of the current game state for {@link #player2}
   */
  public long evaluatePlayer2Utility() {
    return evaluatePlayerUtility(player2);
  }

  /**
   * Delegates to {@link Board#getHash()} to compute a unique hash string for the current board state.
   *
   * @return a unique hash string for the current board state
   */
  public String getBoardHash() {
    return board.getHash();
  }

  /**
   * Create a copy of the game with a new game ID and new players.
   *
   * @param newGameId  the game ID that will be assigned to the copy
   * @param newPlayer1 the first player in the copy
   * @param newPlayer2 the second player in the copy
   *
   * @return a copy of the game
   */
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

  /**
   * Delegates to {@link #getCopy(long, Player, Player)} to create an identical copy of the game.
   *
   * @return an identical copy of the game
   */
  public Game getCopy() {
    return getCopy(gameId, player1, player2);
  }

  /**
   * Get a string containing information about the current state of the game, including:
   *   - dimension
   *   - length required to win
   *   - current utility of each player
   *   - whether the game is in a valid state
   *   - textual representation of the board
   *
   * @return a string representing the game state and board
   */
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

  /**
   * Check whether the given object is a {@link Game} with the same {@link #dim}, {@link #winLength}, {@link #gameId},
   * players, and board.
   *
   * @param o the other object
   *
   * @return {@code true} if the given object is a {@link Game} with identical attributes, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !getClass().equals(o.getClass())) {
      return false;
    }
    final Game other = (Game) o;
    return dim == other.dim && winLength == other.winLength && gameId == other.gameId
      && Objects.equals(player1, other.player1) && Objects.equals(player2, other.player2)
      && Objects.equals(board, other.board);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dim, winLength, gameId, player1, player2, board);
  }
}
