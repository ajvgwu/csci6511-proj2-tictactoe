package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.EmptyCellFilter;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import retrofit2.Call;
import retrofit2.Response;

public class OnlineMoveMaker extends AbstractCellChooser {

  public static final String API_TYPE_MOVE = "move";

  private final CaseByCaseChooser chooser;

  public OnlineMoveMaker() {
    chooser = new CaseByCaseChooser(new AbpTimeLimitChooser(110, new EmptyCellFilter()));
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final long gameId = game.getGameId();
    final Player curPlayer = game.getNextPlayer();
    final int curPlayerId = curPlayer.getId();

    // Select a cell to play and send to server
    Cell choice = chooser.chooseCell(input, game);
    if (choice == null) {
      choice = game.getBoard().getEmptyCells().stream().findAny().orElse(null);
    }
    final String moveCoords = String.valueOf(choice.getRowIdx()) + "," + String.valueOf(choice.getColIdx());
    while (true) {
      final Call<Map> call = API.getApiService().post(API_TYPE_MOVE, String.valueOf(curPlayerId),
        String.valueOf(gameId), moveCoords);
      try {
        final Response<Map> response = call.execute();
        final Map<?, ?> body = response.body();
        final Object o = body.get(OnlineMoveFetcher.API_RESPONSEKEY_CODE);
        if (o instanceof String && o.equals(OnlineMoveFetcher.API_CODE_SUCCESS)) {
          return choice;
        }
        Thread.sleep(1000);
      }
      catch (IOException | InterruptedException e) {
        Logger.error(e, "could not send move via API to server");
      }
    }
  }
}
