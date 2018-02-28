package edu.gwu.ai.codeknights.tictactoe.selector;

public class Cell {

  private final int rowIdx;
  private final int colIdx;

  private boolean isEmpty;
  private final Player player;

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
  }

  @Override
  public String toString() {
    final char value = player != null ? player.getMarker() : '-';
    return String.valueOf(value);
  }
}
