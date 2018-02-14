package edu.gwu.ai.codeknights.tictactoe;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pmw.tinylog.Logger;

public class Main {

  public static void main(final String[] args) throws DimensionException, StateException {
    // Default values
    int dim = 3;
    int winLength = 3;
    String[] stateArgs = null;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help").desc("print this usage information").build();
    final Option dimOpt = Option.builder("d").longOpt("dim").hasArg()
      .desc("board dimension (default is " + String.valueOf(dim) + ")").build();
    final Option winLengthOpt = Option.builder("l").longOpt("win-length").hasArg()
      .desc("length of sequence required to win (default is " + String.valueOf(winLength) + ")").build();
    final Option stateOpt = Option.builder().longOpt("state").hasArgs()
      .desc("initial state of board (default is an empty board)").build();

    final Options options = new Options();
    options.addOption(helpOpt);
    options.addOption(dimOpt);
    options.addOption(winLengthOpt);
    options.addOption(stateOpt);

    // Parse command-line options
    final CommandLineParser parser = new DefaultParser();
    CommandLine line = null;
    Logger.trace("parsing command-line options");
    try {
      line = parser.parse(options, args);
    }
    catch (final ParseException e) {
      Logger.error(e, "error while parsing command-line options");
    }
    if (line != null) {
      if (line.hasOption(dimOpt.getLongOpt())) {
        final String dimVal = line.getOptionValue(dimOpt.getLongOpt());
        try {
          dim = Integer.parseInt(dimVal);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse dim: " + dimVal);
        }
      }
      if (line.hasOption(winLengthOpt.getLongOpt())) {
        final String winLengthVal = line.getOptionValue(winLengthOpt.getLongOpt());
        try {
          winLength = Integer.parseInt(winLengthVal);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse winLength: " + winLengthVal);
        }
      }
      if (line.hasOption(stateOpt.getLongOpt())) {
        stateArgs = line.getOptionValues(stateOpt.getLongOpt());
      }
    }
    if (line == null || line.hasOption(helpOpt.getLongOpt())) {
      // Print usage information
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(" ", options);
      if (line == null) {
        System.exit(1);
      }
    }
    else {
      // Create the game
      final Integer[][] board = new Integer[dim][dim];
      if (stateArgs != null) {
        for (int i = 0; i < dim; i++) {
          int idx = i * dim;
          if (idx >= stateArgs.length) {
            break;
          }
          for (int j = 0; j < dim; j++) {
            idx = i * dim + j;
            if (idx >= stateArgs.length) {
              break;
            }
            final String curArg = stateArgs[idx].trim();
            try {
              board[i][j] = Integer.parseInt(curArg);
            }
            catch (final NumberFormatException e) {
              if (curArg.equalsIgnoreCase(String.valueOf(TicTacToeGame.FIRST_PLAYER_CHAR))) {
                board[i][j] = TicTacToeGame.FIRST_PLAYER_VALUE;
              }
              else if (curArg.equalsIgnoreCase(String.valueOf(TicTacToeGame.OTHER_PLAYER_CHAR))) {
                board[i][j] = TicTacToeGame.OTHER_PLAYER_VALUE;
              }
              else {
                board[i][j] = null;
              }
            }
          }
        }
      }
      final TicTacToeGame game = new TicTacToeGame(dim, winLength, board);

      // TODO: actually play the game; create Solver class that selects the best next move
      // TODO: minimax algorithm?
      // TODO: alpha beta pruning algorithm?
      // TODO: graph-based search by hashing and storing visited already board states?
      // TODO: pre-compute some lookup tables for various board sizes?
      // TODO: how to make best-effort choice if we are running out of time?
      // TODO: is there a general heuristic (for any board size) to choose the BEST FIRST MOVE? i.e., always play close to center?
      // TODO: what is the API to interface w/ the REST/JSON game server?
      Logger.info("Board state:\n{}", game.toString());
      Logger.info("# spaces:       {}={}, {}={}, {}={}", TicTacToeGame.FIRST_PLAYER_CHAR, game.countFirstPlayer(),
        TicTacToeGame.OTHER_PLAYER_CHAR, game.countOtherPlayer(), TicTacToeGame.BLANK_SPACE_CHAR, game.countEmpty());
      Logger.info("Is game over?   {}", game.isGameOver());
      Logger.info("Did anyone win? {}", game.didAnyPlayerWin());
      Logger.info("Who won?        {}={}, {}={}", TicTacToeGame.FIRST_PLAYER_CHAR, game.didFirstPlayerWin(),
        TicTacToeGame.OTHER_PLAYER_CHAR, game.didOtherPlayerWin());
    }
  }
}
