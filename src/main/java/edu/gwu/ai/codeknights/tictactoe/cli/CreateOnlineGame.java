package edu.gwu.ai.codeknights.tictactoe.cli;

import java.util.Map;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import picocli.CommandLine.Command;
import retrofit2.Call;
import retrofit2.Response;

@Command(
  name = "create-online-game", sortOptions = false, showDefaultValues = true,
  description = "create a new online game")
public class CreateOnlineGame extends AbstractOnlineSubcommand {

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Make API call to create new game
    final String teamId1 = String.valueOf(getPlayer1Id());
    final String teamId2 = String.valueOf(getPlayer2Id());
    final Call<Map> call = API.getApiService().postGame(API.API_TYPE_GAME, teamId1, teamId2);
    final Response<Map> response = call.execute();
    final Map<?, ?> body = response.body();
    Logger.debug("body of response: {}", body);
    final Object o = body.get(API.API_RESPONSEKEY_CODE);
    if (o instanceof String) {
      if (o.equals(API.API_CODE_SUCCESS)) {
        final Object gameIdObj = body.get(API.API_RESPONSEKEY_GAMEID);
        System.out.println("created game with gameId=" + String.valueOf(gameIdObj));
      }
    }

    // Done.
    return null;
  }
}
