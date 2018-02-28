package edu.gwu.ai.codeknights.tictactoe.filtering.filter;

import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class InsideFeasibleLineFilter extends EmptyCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    // TODO: finish
    Logger.debug("TODO: implement InsideFeasibleLineSelector.filterCells()");
    return super.filterCells(input, game).filter(cell -> true);
  }
}
