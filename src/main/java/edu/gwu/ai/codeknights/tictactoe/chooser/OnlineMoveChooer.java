package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;

/**
 *
 * fetch a move by invoking api
 *
 * @author zhiyuan
 */
public class OnlineMoveChooer extends AbstractMoveChooser {
    @Override
    public Move findNextMove(Game game) {
        // todo do fetching
        long gameId = game.getId();
        return null;
    }
}
