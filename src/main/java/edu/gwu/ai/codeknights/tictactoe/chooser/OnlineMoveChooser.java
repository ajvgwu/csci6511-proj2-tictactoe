package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

public class OnlineMoveChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    //final long gameId = game.getGameId();
    //TODO: do fetching from online game server (poll?)
    Logger.error("TODO: do fetching from online game server (poll?)");
    return null;
  }
}
