package edu.gwu.ai.codeknights.tictactoe.gui.helpers;

import edu.gwu.ai.codeknights.tictactoe.chooser.*;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.controller.GameMode;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Const;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.pmw.tinylog.Logger;

/**
 * @author zhiyuan
 */
public class MainHelper {

    private Game game;
    private Player master;
    private Player opponent;

    public StringProperty history;

    public MainHelper() {
        game = null;
        master = null;
        opponent = null;

        history = new SimpleStringProperty("");
    }

    public void createLocalGame(long gameId, int dim, int winLen, GameMode
            mode, int masterId, int opId, boolean isHome, boolean asSpectator) {

        // create the choosers for the two players
        // for AI players, use time-limited alpha-beta pruning with a limit of 110 seconds (1min 50sec)
        AbstractCellChooser masterChooser = null;
        AbstractCellChooser opChooser = null;
        switch (mode) {
            case EVE_ONLINE: {
                if(asSpectator){
                    masterChooser = new OnlineMoveFetcher();
                }
                opChooser = new OnlineMoveFetcher();
                break;
            }
            default:
                Logger.error("Invalid Game Mode");
        }

        // create players
        master = new Player(masterId, Const.MASTER_PLAYER_CHAR);
        master.setChooser(masterChooser);
        opponent = new Player(opId, Const.OPPONENT_PLAYER_CHAR);
        opponent.setChooser(opChooser);

        // create game
        this.game = new Game(dim, winLen, gameId, master, opponent, isHome);
    }

    public Player getNextPlayer() {
        return game.getNextPlayer();
    }

    public Game getGame() {
        return game;
    }
}
