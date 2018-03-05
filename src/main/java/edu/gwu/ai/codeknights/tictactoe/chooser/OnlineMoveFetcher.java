package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import retrofit2.Call;
import retrofit2.Response;

public class OnlineMoveFetcher extends AbstractOnlineChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    tryFastForward(game);
    if (game.isGameOver()) {
      return null;
    }
    final long gameId = game.getGameId();
    final int dim = game.getDim();
    final int numCells = dim * dim;
    final int numEmpty = game.getBoard().countEmpty();
    final int numMoves = numCells - numEmpty;
    final int numMovesExpected = Math.max(numCells, numMoves + 1);
    final Player curPlayer = game.getNextPlayer();
    final int curPlayerId = curPlayer.getId();
    while (true) {
      final Call<Map> call = API.getApiService().getMoves(String.valueOf(gameId), numMoves + 1);
      try {
        final Response<Map> response = call.execute();
        final Map<?, ?> body = response.body();
        Object o = body.get(API_RESPONSEKEY_CODE);
        if (o instanceof String && o.equals(API_CODE_SUCCESS)) {
          o = body.get(API_RESPONSEKEY_MOVES);
          if (o instanceof List<?>) {
            final List<?> list = (List<?>) o;
            if (list.size() == numMovesExpected) {
              o = list.get(0);
              if (o instanceof Map<?, ?>) {
                final Map<?, ?> move = (Map<?, ?>) o;
                final Object gameIdObj = move.get(API_MOVEKEY_GAMEID);
                final Object teamIdObj = move.get(API_MOVEKEY_TEAMID);
                final Object moveObj = move.get(API_MOVEKEY_MOVE);
                if (String.valueOf(gameId).equals(gameIdObj) && String.valueOf(curPlayerId).equals(teamIdObj)
                  && moveObj instanceof String) {
                  final Cell cell = game.tryGetCellFromCoord((String) moveObj);
                  if (cell != null) {
                    return cell;
                  }
                }
              }
            }
          }
        }
        Thread.sleep(1000);
      }
      catch (IOException | InterruptedException e) {
        Logger.error(e, "could not get response to API request from server");
      }
    }
  }
}
