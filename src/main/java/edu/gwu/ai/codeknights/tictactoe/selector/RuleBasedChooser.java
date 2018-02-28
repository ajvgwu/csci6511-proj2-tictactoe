package edu.gwu.ai.codeknights.tictactoe.selector;

import org.pmw.tinylog.Logger;

public class RuleBasedChooser implements PlayChooser {

  public static final String NAME = "RuleBasedChooser";

  @Override
  public Play choosePlay(final TicTacToeGame game) {
    final int dim = game.getDim();
    final int winLength = game.getWinLength();
    final Player player = game.getNextPlayer();
    final Board board = game.getBoard();

    // Rule 1: first move goes near the center
    if (board.isEmpty()) {
      final int center = (int) (dim / 2);
      return new Play(player, board.getCell(center, center));
    }

    // Rule 2: win if immediately possible
    board.getLinesAtLeastLength(winLength).parallelStream()
      .filter(line -> true)
      .findFirst()
      .orElse(null);
    // TODO

    // Rule 3: block opponent wins at both ends of lines
    // TODO

    // Rule 4: create a winnable fork (win in two ways)
    // TODO

    // Rule 5: prevent opponent fork
    // TODO

    // Rule 6: maximize win potential
    // TODO

    // TODO: finish
    Logger.debug("TODO: finish RuleBasedChooser.choosePlay()");
    return null;
  }
}
