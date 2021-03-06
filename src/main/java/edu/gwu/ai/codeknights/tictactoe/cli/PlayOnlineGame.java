package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractOnlineChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveFetcher;
import edu.gwu.ai.codeknights.tictactoe.chooser.OnlineMoveMaker;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "play-online-game", sortOptions = false, showDefaultValues = true,
  description = "play an existing online game")
public class PlayOnlineGame extends AbstractOnlineSubcommand {

  @Option(
    names = {"--player2"},
    description = "play as player2 (instead of player1)")
  private boolean player2 = false;

  @Option(
    names = {"--spectator"},
    description = "do not play as either player, just spectate and display moves that are made by the players")
  private boolean spectator = false;

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
    player2 = !!player2;
    spectator = !!spectator;
    timeLimitSec = Math.max(0, timeLimitSec);
    if (timeLimitSec < 1) {
      throw new IllegalArgumentException("timeLimitSec must be >= 1");
    }
  }

  protected boolean isPlayer2() {
    return player2;
  }

  protected boolean isSpectator() {
    return spectator;
  }

  protected int getTimeLimitSec() {
    return timeLimitSec;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Instantiate game
    final Game game = createGame(true);

    // Configure local player
    Player localPlayer = game.getPlayer1();
    if (isPlayer2()) {
      localPlayer = game.getPlayer2();
    }
    if (isSpectator()) {
      localPlayer.setChooser(new OnlineMoveFetcher());
    }
    else {
      localPlayer.setChooser(new OnlineMoveMaker(getTimeLimitSec()));
    }

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
