package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractOnlineChooser extends AbstractCellChooser {

  public static void tryFastForward(final Game game) {
    final long gameId = game.getGameId();
    final int dim = game.getDim();
    final int numCells = dim * dim;
    final Call<Map> call = API.getApiService().getMoves(String.valueOf(gameId), numCells);
    try {
      final Response<Map> response = call.execute();
      Logger.debug("got response from server: {}", response);
      final Map<?, ?> body = response.body();
      Logger.debug("body of response: {}", body);
      Object o = body.get(API.API_RESPONSEKEY_CODE);
      if (o instanceof String) {
        if (o.equals(API.API_CODE_SUCCESS)) {
          o = body.get(API.API_RESPONSEKEY_MOVES);
          if (o instanceof List<?>) {
            Player curPlayer = game.getPlayer1();
            final List<?> moves = (List<?>) o;
            for (int i = moves.size() - 1; i >= 0; i--) {
              final Object item = moves.get(i);
              if (item instanceof Map<?, ?>) {
                final Map<?, ?> move = (Map<?, ?>) item;
                final Object gameIdObj = move.get(API.API_MOVEKEY_GAMEID);
                final Object moveObj = move.get(API.API_MOVEKEY_MOVE);
                if (String.valueOf(gameId).equals(gameIdObj) && moveObj instanceof String) {
                  final Cell cell = game.tryGetCellFromCoord((String) moveObj);
                  if (cell != null) {
                    final Object teamIdObj = move.get(API.API_MOVEKEY_TEAMID);
                    if (!String.valueOf(curPlayer.getId()).equals(teamIdObj)) {
                      Logger.warn("moves from server might be out of order");
                      final Player otherPlayer = game.getOtherPlayer(curPlayer);
                      if (String.valueOf(otherPlayer.getId()).equals(teamIdObj)) {
                        curPlayer = otherPlayer;
                      }
                    }
                    cell.setPlayer(curPlayer);
                  }
                }
              }
              curPlayer = game.getOtherPlayer(curPlayer);
            }
          }
        }
        else {
          final Object msgObj = body.get(API.API_RESPONSEKEY_MESSAGE);
          Logger.error("got response {} from server with message: {}", o, msgObj);
        }
      }
    }
    catch (final IOException e) {
      Logger.error(e, "error while fetching moves from server to fast-forward game");
    }
  }
}
