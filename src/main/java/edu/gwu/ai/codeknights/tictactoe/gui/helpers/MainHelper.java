package edu.gwu.ai.codeknights.tictactoe.gui.helpers;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractCellChooser;
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

        // create the choosers for the two players
        AbstractCellChooser masterChooser = null;
        AbstractCellChooser opChooser = null;
        switch (mode) {
            case PVP: {
                masterChooser = new StupidMoveChooser();
                opChooser = new StupidMoveChooser();
                break;
            }
            case PVE: {
                masterChooser = new StupidMoveChooser();
                opChooser = new CaseByCaseChooser();
                break;
            }
            case EVP: {
                masterChooser = new CaseByCaseChooser();
                opChooser = new StupidMoveChooser();
                break;
            }
            case EVE: {
                masterChooser = new CaseByCaseChooser();
                opChooser = new CaseByCaseChooser();
                break;
            }
            case EVE_ONLINE: {
                masterChooser = new CaseByCaseChooser();
                opChooser = new OnlineMoveChooser();
                break;
            }
        }

        // create players
        master = new Player(masterId, Const.MASTER_PLAYER_CHAR);
        master.setChooser(masterChooser);
        opponent = new Player(opId, Const.OPPONENT_PLAYER_CHAR);
        opponent.setChooser(opChooser);

        // create game
        this.game = new Game(dim, winLen, gameId, master, opponent);
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
