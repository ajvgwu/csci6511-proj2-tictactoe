package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import picocli.CommandLine.Option;

public abstract class AbstractOfflineSubcommand extends AbstractSubcommand {

  @Option(
    names = {"-s", "--state"},
    description = "initial board state")
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
  protected Game createGame() {
    final Game game = super.createGame();
    game.populate(getStateArgs());
    return game;
  }
}
