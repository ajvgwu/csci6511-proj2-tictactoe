package edu.gwu.ai.codeknights.tictactoe.gui.util;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;
import javafx.concurrent.Task;

/**
 * @author zhiyuan
 */
public class  FetchMoveTask extends Task {

    private Player player;
    private Game game;

    public FetchMoveTask(Game game, Player player){
        this.game = game;
        this.player = player;
    }

    @Override
    protected Move call(){
        return player.getMoveChooser().findNextMove(game);
    }
}
