package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class EmptyCellFilter extends AbstractCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return input.filter(Cell::isEmpty);
  }
}
