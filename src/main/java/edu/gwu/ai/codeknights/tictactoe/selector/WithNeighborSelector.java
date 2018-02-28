package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.gwu.ai.codeknights.tictactoe.core.Game;

public class WithNeighborSelector implements CellSelector, CellFilter {

  public static boolean hasPopulatedNeighbor(final int rowIdx, final int colIdx, final Game game) {
    final int dim = game.getDim();
    return rowIdx > 0 && colIdx > 0 && game.getCellValue(rowIdx - 1, colIdx - 1) != null
      || rowIdx > 0 && game.getCellValue(rowIdx - 1, colIdx) != null
      || rowIdx > 0 && colIdx < dim - 1 && game.getCellValue(rowIdx - 1, colIdx + 1) != null
      || colIdx > 0 && game.getCellValue(rowIdx, colIdx - 1) != null
      || colIdx < dim - 1 && game.getCellValue(rowIdx, colIdx + 1) != null
      || rowIdx < dim - 1 && colIdx > 0 && game.getCellValue(rowIdx + 1, colIdx - 1) != null
      || rowIdx < dim - 1 && game.getCellValue(rowIdx + 1, colIdx) != null
      || rowIdx < dim - 1 && colIdx < dim - 1 && game.getCellValue(rowIdx + 1, colIdx + 1) != null;
  }

  public static boolean hasPopulatedNeighbor(final Cell cell, final Game game) {
    return hasPopulatedNeighbor(cell.getRowIdx(), cell.getColIdx(), game);
  }

  @Override
  public List<Cell> selectCells(final Game game) {
    final List<Cell> cells = new ArrayList<>();
    final int dim = game.getDim();
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (game.getCellValue(i, j) == null && hasPopulatedNeighbor(i, j, game)) {
          cells.add(new Cell(i, j));
        }
      }
    }
    return cells;
  }

  @Override
  public List<Cell> filterCells(final List<Cell> input, final Game game) {
    return input.stream()
      .filter(cell -> game.getCellValue(cell.getRowIdx(), cell.getColIdx()) == null && hasPopulatedNeighbor(cell, game))
      .collect(Collectors.toList());
  }
}
