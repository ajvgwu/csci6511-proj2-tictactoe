package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "single-move", sortOptions = false, showDefaultValues = true,
  description = "play the next move for the current board state")
public class SingleMove extends AbstractOfflineSubcommand {

  @Option(
    names = {"--chooser"},
    description = "chooser used to select the next move")
  private Chooser chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    if (chooser == null) {
      chooser = Chooser.CASE_BY_CASE_ABP_LIMIT_120SEC;
      throw new IllegalArgumentException("must select chooser");
    }
  }

  protected Chooser getChooser() {
    return chooser;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Create game
    final Game game = createGame(true);

    // Configure the chooser for whichever player is next
    game.getNextPlayer().setChooser(chooser.createChooser());

    // Play the next move
    final GameplayHelper helper = new GameplayHelper(game);
    helper.singleMove();

    // Done.
    return null;
  }
}
