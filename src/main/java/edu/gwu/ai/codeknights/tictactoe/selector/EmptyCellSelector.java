package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.gwu.ai.codeknights.tictactoe.core.Game;

public class EmptyCellSelector implements CellSelector, CellFilter {

  @Override
  public List<Cell> selectCells(final Game game) {
    final List<Cell> cells = new ArrayList<>();
    final int dim = game.getDim();
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (game.getCellValue(i, j) == null) {
          cells.add(new Cell(i, j));
        }
      }
    }
    return cells;
  }

  @Override
  public List<Cell> filterCells(final List<Cell> input, final Game game) {
    return input.stream().filter(cell -> game.getCellValue(cell.getRowIdx(), cell.getColIdx()) == null)
      .collect(Collectors.toList());
  }
}
