package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class EmptyCellSelector implements CellSelector, CellFilter {

  @Override
  public Stream<Cell> selectCells(final TicTacToeGame game) {
    return game.getBoard().getAllCells().parallelStream().filter(cell -> cell.isEmpty());
  }

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return input.filter(cell -> cell.isEmpty());
  }
}
