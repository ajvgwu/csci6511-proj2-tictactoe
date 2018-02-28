package edu.gwu.ai.codeknights.tictactoe.selector;

import edu.gwu.ai.codeknights.tictactoe.core.Game;

public class Cell {

  private final int rowIdx;
  private final int colIdx;

  public Cell(final int rowIdx, final int colIdx) {
    this.rowIdx = rowIdx;
    this.colIdx = colIdx;
  }

  public int getRowIdx() {
    return rowIdx;
  }

  public int getColIdx() {
    return colIdx;
  }

  public Integer getCellValue(final Game game) {
    return game.getCellValue(rowIdx, colIdx);
  }
}
