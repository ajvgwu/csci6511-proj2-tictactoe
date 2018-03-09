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
  private boolean isHome;

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
  public Game(final int dim, final int winLength, final long gameId, final
  Player player1, final Player player2, boolean isHome) {
    this.dim = dim;
    this.winLength = winLength;
    this.gameId = gameId;
    this.player1 = player1;
    this.player2 = player2;
    this.isHome = isHome;

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

  public Player getPlayer(int id){
    Player player = null;
    if(this.getPlayer1().getId() == id){
      player = this.getPlayer1();
    }
    if(this.getPlayer2().getId() == id){
      player = this.getPlayer2();
    }

    return player;
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
    if((p1Count == p2Count && isHome) || p1Count < p2Count){
      return player1;
    }else{
      return player2;
    }
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
