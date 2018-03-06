package edu.gwu.ai.codeknights.tictactoe.cli;

import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import picocli.CommandLine.Command;

@Command(
  name = "launch-gui", sortOptions = false, showDefaultValues = true,
  description = "launch the GUI")
public class LaunchGui extends AbstractSubcommand {

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Launch GUI with no args
    TicTacToe.main(new String[0]);

    // Done.
    return null;
  }
}
