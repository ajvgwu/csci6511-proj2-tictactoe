package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public abstract class AbstractCellChooser {

  /**
   * From a stream of cells, choose the "best" cell for the current player, according to some criteria or heuristic
   * @param input a stream of cells
   * @return the chosen "best" cell
   */
  public abstract Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game);

  /**
   * From a game state, choose the "best" cell for the current player, according to some criteria or heuristic
   * @param game a game state
   * @return the chosen "best" cell
   */
  public final Cell chooseCell(final TicTacToeGame game) {
    return chooseCell(game.getBoard().getEmptyCells().stream(), game);
  }
}
