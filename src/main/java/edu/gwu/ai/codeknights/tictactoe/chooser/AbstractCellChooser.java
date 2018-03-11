package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractCellChooser {

  /**
   * From a stream of cells, choose the "best" cell for the current player, according to some criteria or heuristic
   * @param input a stream of cells
   * @return the chosen "best" cell
   */
  public abstract Cell chooseCell(final Stream<Cell> input, final Game game);

  /**
   * From a game state, choose the "best" cell for the current player, according to some criteria or heuristic
   * @param game a game state
   * @return the chosen "best" cell
   */
  public final Cell chooseCell(final Game game) {
    Cell cell = chooseCell(game.getBoard().getEmptyCells().stream(), game);
    if(cell == null){
      cell = chooseRandom(game);
    }
    return cell;
  }

  public final Cell chooseRandom(final Game game){
    Cell cell = null;
    List<Cell> emptyCells = game.getBoard().getEmptyCells();
    Collections.shuffle(emptyCells);
    if(emptyCells.size() != 0){
      cell = emptyCells.get(0);
    }
    return cell;
  }
}
