package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "finish-game", sortOptions = false, showDefaultValues = true,
  description = "finish the game from the current board state")
public class FinishGame extends AbstractOfflineSubcommand {

  @Option(
    names = {"--player1-chooser"},
    description = "chooser used to select moves for player1")
  private Chooser player1Chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;

  @Option(
    names = {"--player2-chooser"},
    description = "chooser used to select moves for player2")
  private Chooser player2Chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    if (player1Chooser == null) {
      player1Chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;
      throw new IllegalArgumentException("must select player1Chooser");
    }
    if (player2Chooser == null) {
      player2Chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;
      throw new IllegalArgumentException("must select player2Chooser");
    }
  }

  protected Chooser getPlayer1Chooser() {
    return player1Chooser;
  }

  protected Chooser getPlayer2Chooser() {
    return player2Chooser;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Create game
    final Game game = createGame(true);

    // Configure choosers for both players
    game.getPlayer1().setChooser(getPlayer1Chooser().createChooser());
    game.getPlayer2().setChooser(getPlayer2Chooser().createChooser());

    // Finish the game
    final GameplayHelper helper = new GameplayHelper(game);
    helper.finishGame();

    // Done.
    return null;
  }
}
