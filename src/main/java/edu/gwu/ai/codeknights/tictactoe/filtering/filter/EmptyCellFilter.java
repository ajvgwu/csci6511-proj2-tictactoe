package edu.gwu.ai.codeknights.tictactoe.filtering.filter;

import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class EmptyCellFilter extends AbstractCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return input.filter(Cell::isEmpty);
  }
}
