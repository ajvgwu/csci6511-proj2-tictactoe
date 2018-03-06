package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractOnlineChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveFetcher;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveMaker;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.gui.util.API;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "online-game", sortOptions = false, showDefaultValues = true,
  description = "play an online game")
public class OnlineGame extends AbstractSubcommand {

  @Option(
    names = {"--user-id"}, required = true,
    description = "userId for online game server")
  private String userId = null;

  @Option(
    names = {"--api-key"}, required = true,
    description = "apiKey for online game server")
  private String apiKey = null;

  @Option(
    names = {"--player2"},
    description = "play as player2 (instead of player1)")
  private boolean player2 = false;

  @Option(
    names = {"--time-limit-sec"},
    description = "time limit (in seconds) for choosing a move")
  private int timeLimitSec = 110;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    if (getGameId() < 1000) {
      throw new IllegalArgumentException("gameId must be >= 1000");
    }
    if (getPlayer1Id() < 1000) {
      throw new IllegalArgumentException("player1Id must be >= 1000");
    }
    if (getPlayer2Id() < 1000) {
      throw new IllegalArgumentException("player2Id must be >= 1000");
    }
    userId = userId != null ? userId.trim() : new String();
    if (userId.length() < 1) {
      throw new IllegalArgumentException("userId must be provided");
    }
    apiKey = apiKey != null ? apiKey.trim() : new String();
    if (apiKey.length() < 1) {
      throw new IllegalArgumentException("apiKey must be provided");
    }
    player2 = !!player2;
    timeLimitSec = Math.max(0, timeLimitSec);
    if (timeLimitSec < 1) {
      throw new IllegalArgumentException("timeLimitSec must be >= 1");
    }
  }

  protected String getUserId() {
    return userId;
  }

  protected String getApiKey() {
    return apiKey;
  }

  protected boolean isPlayer2() {
    return player2;
  }

  protected int getTimeLimitSec() {
    return timeLimitSec;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Populate global constants
    API.HEADER_USER_ID_VALUE = getUserId();
    API.HEADER_API_KEY_VALUE = getApiKey();

    // Create game
    final Game game = createGame();

    // Configure local player
    Player localPlayer = game.getPlayer1();
    if (isPlayer2()) {
      localPlayer = game.getPlayer2();
    }
    localPlayer.setChooser(new OnlineMoveMaker(getTimeLimitSec()));

    // Configure remote player
    final Player remotePlayer = game.getOtherPlayer(localPlayer);
    remotePlayer.setChooser(new OnlineMoveFetcher());

    // Fast-forward game to current state
    AbstractOnlineChooser.tryFastForward(game);

    // Play the game
    final GameplayHelper helper = new GameplayHelper(game);
    while (!game.isGameOver()) {
      helper.singleMove();
    }

    // Done.
    return null;
  }
}
