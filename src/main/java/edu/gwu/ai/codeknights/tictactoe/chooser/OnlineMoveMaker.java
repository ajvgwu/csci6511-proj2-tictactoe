package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.util.Const;
import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import retrofit2.Call;
import retrofit2.Response;

public class OnlineMoveMaker extends AbstractOnlineChooser {

  private final CaseByCaseChooser chooser;

  public OnlineMoveMaker(final int limitSec) {
    // For 2-min move time limit, leave 20 seconds for overhead (e.g., fast-forwarding game)
    chooser = new CaseByCaseChooser(new AbpTimeLimitChooser(limitSec));
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    if (game.isGameOver()) {
      Logger.debug("no more moves, game is over");
      return null;
    }
    final long gameId = game.getGameId();
    final Player curPlayer = game.getNextPlayer();
    final int curPlayerId = curPlayer.getId();

    // Select a cell to play and send to server
    Logger.debug("choosing next move");
    Cell choice = chooser.chooseCell(input, game);
    if (choice == null) {
      Logger.debug("no cell found, choosing any empty cell");
      choice = game.getBoard().getEmptyCells().stream().findAny().orElse(null);
    }
    final String moveCoords = String.valueOf(choice.getRowIdx()+ Const.ONLINE_BOARD_OFFSET) + "," + String.valueOf(choice.getColIdx()+Const.ONLINE_BOARD_OFFSET);
    while (true) {
      final Call<Map> call = API.getApiService().post(API.API_TYPE_MOVE, String.valueOf(curPlayerId),
        String.valueOf(gameId), moveCoords);
      try {
        Logger.debug("sending move to server: {}", moveCoords);
        final Response<Map> response = call.execute();
        Logger.debug("got response from server: {}", response);
        final Map<?, ?> body = response.body();
        Logger.debug("body of response: {}", body);
        final Object o = body.get(API.API_RESPONSEKEY_CODE);
        if (o instanceof String) {
          if (o.equals(API.API_CODE_SUCCESS)) {
            Logger.debug("response successful, returning move in cell: {}", choice);
            return choice;
          }
          else {
            final Object msgObj = body.get(API.API_RESPONSEKEY_MESSAGE);
            Logger.error("got response {} from server with message: {}", o, msgObj);
          }
        }
        Thread.sleep(1000);
      }
      catch (IOException | InterruptedException e) {
        Logger.error(e, "error while sending move to server");
      }
    }
  }
}
