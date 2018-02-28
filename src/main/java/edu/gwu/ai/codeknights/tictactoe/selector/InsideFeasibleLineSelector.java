package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class InsideFeasibleLineSelector implements CellSelector, CellFilter {

  @Override
  public Stream<Cell> selectCells(final TicTacToeGame game) {
    // TODO: finish
    return game.getBoard().getAllCells().parallelStream();
  }

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    // TODO: finish
    return input;
  }
}
