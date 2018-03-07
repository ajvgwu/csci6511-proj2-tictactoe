package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import picocli.CommandLine.Option;

public abstract class AbstractOfflineSubcommand extends AbstractSubcommand {

  @Option(
    names = {"-s", "--state"}, arity = "0..*",
    description = "initial board state; player1 moves given by \"X\"; player2 moves given by \"O\"; empty cells given by any other string")
  private String[] stateArgs = null;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    stateArgs = stateArgs != null ? stateArgs : new String[0];
  }

  protected String[] getStateArgs() {
    return stateArgs;
  }

  @Override
  protected Game createGame(boolean isHome) {
    final Game game = super.createGame(isHome);
    game.populate(getStateArgs());
    return game;
  }
}
