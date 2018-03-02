package edu.gwu.ai.codeknights.tictactoe.gui.helpers;

import edu.gwu.ai.codeknights.tictactoe.chooser.CaseByCaseChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.StupidMoveChooser;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.controller.GameMode;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    public void createGame(long gameId, int dim, int winLen, GameMode mode, int masterId, int opId) {

        // set player symbols
        final char masterSymbol = Const.MASTER_PLAYER_CHAR;
        final char opSymbol = Const.OPPONENT_PLAYER_CHAR;

        // create two players
        switch (mode) {
            case PVE: {
                // pve
                this.master = new Player(masterId, masterSymbol);
                this.master.setChooser(new StupidMoveChooser());
                this.opponent = new Player(opId, opSymbol);
                this.opponent.setChooser(new CaseByCaseChooser());
                break;
            }
            case EVE: {
                // eve
                this.master = new Player(masterId, masterSymbol);
                this.master.setChooser(new CaseByCaseChooser());
                this.opponent = new Player(opId, opSymbol);
                this.opponent.setChooser(new CaseByCaseChooser());
                break;
            }
            case EVE_ONLINE: {
                // eve online
                this.master = new Player(masterId, masterSymbol);
                this.master.setChooser(new CaseByCaseChooser());
                this.opponent = new Player(opId, opSymbol);
                this.opponent.setChooser(new OnlineMoveChooser());
                break;
            }
        }

        // create game
        this.game = new Game(dim, winLen, gameId, this.master, this.opponent);
    }

    public Player getNextPlayer() {
        return game.getNextPlayer();
    }

    public Game getGame() {
        return game;
    }

    public Player getMaster() {
        return master;
    }

    public Player getOpponent() {
        return opponent;
    }
}
