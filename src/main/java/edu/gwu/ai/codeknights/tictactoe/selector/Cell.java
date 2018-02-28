package edu.gwu.ai.codeknights.tictactoe.selector;

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
    final char value = player != null ? player.getMarker() : '-';
    return String.valueOf(value);
  }
}
