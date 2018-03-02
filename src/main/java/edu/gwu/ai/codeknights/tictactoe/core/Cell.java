package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.Objects;

import edu.gwu.ai.codeknights.tictactoe.util.Const;

public class Cell {

  private final int rowIdx;
  private final int colIdx;

  private boolean isEmpty;
  private Player player;

  public Cell(final int rowIdx, final int colIdx) {
    this.rowIdx = rowIdx;
    this.colIdx = colIdx;

    isEmpty = true;
    player = null;
  }

  public int getRowIdx() {
    return rowIdx;
  }

  public int getColIdx() {
    return colIdx;
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(final Player player) {
    isEmpty = player == null;
    this.player = player;
  }

  public boolean isPopulated() {
    return player != null;
  }

  public boolean isPopulatedBy(final Player player) {
    return this.player != null && this.player.equals(player);
  }

  @Override
  public String toString() {
    final char value = player != null ? player.getMarker() : Const.BLANK_SPACE_CHAR;
    return String.valueOf(value);
  }

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
