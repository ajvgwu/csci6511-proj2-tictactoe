package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public abstract class AbstractCellFilter {

  /**
   * From a stream of cells, perform additional filtering according to some criteria or heuristic
   * @param input a stream of cells
   * @return the filtered stream of cells
   */
  public abstract Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game);

  /**
   * From a game state, select a stream of cells matching some criteria or heuristic
   * @param game a game state
   * @return the selected stream of cells
   */
  public final Stream<Cell> filterCells(final TicTacToeGame game) {
    return filterCells(game.getBoard().getEmptyCells().parallelStream(), game);
  }
}
