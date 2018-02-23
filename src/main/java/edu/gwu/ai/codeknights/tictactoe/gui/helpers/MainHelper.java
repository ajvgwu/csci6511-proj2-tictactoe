package edu.gwu.ai.codeknights.tictactoe.gui.helpers;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractMoveChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveChooer;
import edu.gwu.ai.codeknights.tictactoe.chooser.ParallelAlphaBetaPruningChooser;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.StupidMoveChooser;
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

    public StringProperty history = new SimpleStringProperty("");

    public void createGame(long gameId, int rowLen, int cowLen, int winLen, int mode, int masterId, int opId) {

        String masterSymbol;
        AbstractMoveChooser masterMoveChooser;
        String opSymbol;
        AbstractMoveChooser opMoveChooser;

        // set player symbols
        masterSymbol = Const.PLAYER_SYMBOL_MASTER;
        opSymbol = Const.PLAYER_SYMBOL_OPPONENT;

        // create two players
        switch (mode) {
            case 1:
                // pve
                masterMoveChooser = new StupidMoveChooser();
                this.master = new Player(masterId, masterSymbol, masterMoveChooser);
                opMoveChooser = new ParallelAlphaBetaPruningChooser();
                this.opponent = new Player(opId, opSymbol, opMoveChooser);
                break;
            case 2:
                // eve
                masterMoveChooser = new ParallelAlphaBetaPruningChooser();
                this.master = new Player(masterId, masterSymbol, masterMoveChooser);
                opMoveChooser = new ParallelAlphaBetaPruningChooser();
                this.opponent = new Player(opId, opSymbol, opMoveChooser);
                break;
            case 3:
                // eve online
                masterMoveChooser = new ParallelAlphaBetaPruningChooser();
                this.master = new Player(masterId, masterSymbol, masterMoveChooser);
                opMoveChooser = new OnlineMoveChooer();
                this.opponent = new Player(opId, opSymbol, opMoveChooser);
                break;
            default:
                System.out.println("Invalid Mode");
        }

        // create game matrix
        Integer[][] board = new Integer[rowLen][cowLen];
        try {
            this.game = new Game(gameId, rowLen, winLen, board, masterId, opId);
        } catch (DimensionException | StateException e) {
            e.printStackTrace();
        }
    }

    public Player getNextPlayer() {
        if (game.getNextPlayer() == master.getId()) {
            return master;
        } else {
            return opponent;
        }
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