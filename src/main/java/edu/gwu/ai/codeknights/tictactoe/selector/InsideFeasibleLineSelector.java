package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

public class InsideFeasibleLineSelector implements CellSelector, CellFilter {

  @Override
  public Stream<Cell> selectCells(final TicTacToeGame game) {
    // TODO: finish
    Logger.debug("TODO: implement InsideFeasibleLineSelector.selectCells()");
    return game.getBoard().getAllCells().parallelStream();
  }

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    // TODO: finish
    Logger.debug("TODO: implement InsideFeasibleLineSelector.filterCells()");
    return input;
  }
}
