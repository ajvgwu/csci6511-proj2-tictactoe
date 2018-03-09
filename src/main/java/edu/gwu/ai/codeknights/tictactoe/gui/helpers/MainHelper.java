package edu.gwu.ai.codeknights.tictactoe.gui.helpers;

import edu.gwu.ai.codeknights.tictactoe.chooser.*;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.EmptyCellFilter;
import edu.gwu.ai.codeknights.tictactoe.gui.controller.GameMode;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.pmw.tinylog.Logger;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

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
            case PVP: {
                masterChooser = new StupidMoveChooser();
                opChooser = new StupidMoveChooser();
                break;
            }
            case PVE: {
                masterChooser = new StupidMoveChooser();
                opChooser = new CaseByCaseChooser(new AbpTimeLimitChooser(110, new EmptyCellFilter()));
                break;
            }
            case EVP: {
                masterChooser = new CaseByCaseChooser(new AbpTimeLimitChooser(110, new EmptyCellFilter()));
                opChooser = new StupidMoveChooser();
                break;
            }
            case EVE: {
                masterChooser = new CaseByCaseChooser(new AbpTimeLimitChooser(110, new EmptyCellFilter()));
                opChooser = new CaseByCaseChooser(new AbpTimeLimitChooser(110, new EmptyCellFilter()));
                break;
            }
            case EVE_ONLINE: {
                if(asSpectator){
                    masterChooser = new OnlineMoveFetcher();
                }else{
                    masterChooser = new OnlineMoveMaker(110);
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

    public long createOnelineGame(int team1Id, int team2Id) {
        long gameId = 0L;
        final Call<Map> call = API.getApiService().postGame(API.API_TYPE_GAME,
                String.valueOf(team1Id), String.valueOf(team2Id));
        final Response<Map> response;
        try {
            response = call.execute();
            final Map<?, ?> body = response.body();
            Logger.debug("body of response: {}", body);
            if (body == null) {
                gameId = -1L;
            } else {
                final Object o = body.get(API.API_RESPONSEKEY_CODE);
                if (API.API_CODE_SUCCESS.equals(String.valueOf(o))) {
                    final Object gameIdObj = body.get(API.API_RESPONSEKEY_GAMEID);
                    String gameIdStr = String.valueOf(gameIdObj);
                    gameId = Double.valueOf(gameIdStr).longValue();
                    System.out.println("created game with gameId=" + String.valueOf(gameIdObj));
                }
            }
        } catch (IOException e) {
            Logger.error("failed in creating game");
        }

        return gameId;
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
