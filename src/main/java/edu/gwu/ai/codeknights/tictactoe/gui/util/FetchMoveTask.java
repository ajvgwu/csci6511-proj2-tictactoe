package edu.gwu.ai.codeknights.tictactoe.gui.util;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import javafx.concurrent.Task;

/**
 * @author zhiyuan
 */
public class FetchMoveTask extends Task<Cell> {

    private Player player;
    private Game game;

    public FetchMoveTask(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    protected Cell call() {
        return player.chooseCell(game);
    }
}
