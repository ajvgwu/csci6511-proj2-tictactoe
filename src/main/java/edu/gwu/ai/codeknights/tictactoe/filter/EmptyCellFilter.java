package edu.gwu.ai.codeknights.tictactoe.filter;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

import java.util.stream.Stream;

public class EmptyCellFilter extends AbstractCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final Game game) {
    return input.filter(Cell::isEmpty);
  }
}
