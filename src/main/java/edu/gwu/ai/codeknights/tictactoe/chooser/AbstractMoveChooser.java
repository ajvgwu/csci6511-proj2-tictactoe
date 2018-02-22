package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;

/**
 * @author zhiyuan
 */
public abstract class AbstractMoveChooser {

    public abstract Move findNextMove(Game game);

}
