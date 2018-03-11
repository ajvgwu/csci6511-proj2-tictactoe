package edu.gwu.ai.codeknights.tictactoe.core;

import edu.gwu.ai.codeknights.tictactoe.util.Const;

import java.util.Objects;

/**
 * A cell on the {@link Board} of a Tic Tac Toe {@link Game}. It knows its location on the board and keeps track of
 * what {@link Player} has populated it.
 *
 * @author ajv
 */
public class Cell {

  private final int rowIdx;
  private final int colIdx;

  private boolean isEmpty;
  private Player player;

  /**
   * Construct a new cell at the given location.
   *
   * @param rowIdx zero-based row index on the board
   * @param colIdx zero-based column index on the board
   */
  public Cell(final int rowIdx, final int colIdx) {
    this.rowIdx = rowIdx;
    this.colIdx = colIdx;

    isEmpty = true;
    player = null;
  }

  /**
   * Get the zero-based row index on the board.
   *
   * @return the row index
   */
  public int getRowIdx() {
    return rowIdx;
  }

  /**
   * Get the zero-based column index on the board.
   *
   * @return the column index
   */
  public int getColIdx() {
    return colIdx;
  }

  /**
   * Check whether the cell is empty (not populated by any player).
   *
   * @return {@code true} if the cell is empty (not populated), {@code false} otherwise
   */
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * Get the player who has populated this cell, if any.
   *
   * @return the player who has populated this cell (may be {@code null})
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Populate this cell with the given player.
   *
   * @param player the player who is populating this cell
   */
  public void setPlayer(final Player player) {
    isEmpty = player == null;
    this.player = player;
  }

  /**
   * Check whether the cell is populated by a player.
   *
   * @return {@code true} if the cell is populated, {@code false} otherwise
   */
  public boolean isPopulated() {
    return player != null;
  }

  /**
   * Check whether the cell is populated by the given player.
   *
   * @param player the player to check for
   *
   * @return {@code true} if the cell is populated by the given player, {@code false} otherwise
   */
  public boolean isPopulatedBy(final Player player) {
    return this.player != null && this.player.equals(player);
  }

  /**
   * If the cell is populated, get the player's marker. Otherwise, get {@link Const#BLANK_SPACE_CHAR}.
   *
   * @return the contents of this cell
   */
  @Override
  public String toString() {
    final char value = player != null ? player.getMarker() : Const.BLANK_SPACE_CHAR;
    return String.valueOf(value);
  }

  /**
   * Check whether the given object is a {@link Cell} with the same {@link #rowIdx} and {@link #colIdx}.
   *
   * @param o the other object
   *
   * @return {@code true} if the given object is a {@link Cell} at the same position, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !getClass().equals(o.getClass())) {
      return false;
    }
    final Cell other = (Cell) o;
    return rowIdx == other.rowIdx &&
      colIdx == other.colIdx;
  }

  @Override
  public int hashCode() {
    return Objects.hash(rowIdx, colIdx);
  }
}
