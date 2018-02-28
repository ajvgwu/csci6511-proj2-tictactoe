package edu.gwu.ai.codeknights.tictactoe;

import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import edu.gwu.ai.codeknights.tictactoe.selector.Player;
import edu.gwu.ai.codeknights.tictactoe.selector.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.util.Const;

public class Main {

  public static void main(final String[] args) throws DimensionException, StateException, InterruptedException {
    // Default values
    int dim = 3;
    int winLength = 3;
    long gameId = new Random().nextInt(1000);
    int player1Id = 1;
    final char player1Marker = Const.MASTER_PLAYER_CHAR;
    int player2Id = 2;
    final char player2Marker = Const.OPPONENT_PLAYER_CHAR;
    boolean randomize = false;
    String[] stateArgs = null;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help").desc("print this usage information").build();
    final Option dimOpt = Option.builder("d").longOpt("dim").hasArg().argName("DIM")
      .desc("board dimension (default is " + String.valueOf(dim) + ")").build();
    final Option winLengthOpt = Option.builder("l").longOpt("win-length").hasArg().argName("LEN")
      .desc("length of sequence required to win (default is " + String.valueOf(winLength) + ")").build();
    final Option gameIdOpt = Option.builder().longOpt("game-id").hasArg().argName("ID").desc("identifier for game")
      .build();
    final Option player1IdOpt = Option.builder().longOpt("player1-id").hasArg().argName("ID")
      .desc("identifier for player 1").build();
    final Option player2IdOpt = Option.builder().longOpt("player2-id").hasArg().argName("ID")
      .desc("identifier for player 2").build();
    final Option randomizeOpt = Option.builder("r").longOpt("randomize")
      .desc("when multiple moves are scored equally, randomly choose from among them").build();
    final Option stateOpt = Option.builder("s").longOpt("state").hasArgs().argName("CELLS")
      .desc("initial state of board (default is an empty board); moves of the first player given by '"
        + String.valueOf(player1Id) + "' or '" + String.valueOf(player1Marker)
        + "'; moves of the second player given by '" + String.valueOf(player2Id) + "' or '"
        + String.valueOf(player2Marker) + "'; empty spaces given by any other character")
      .build();

    final Options options = new Options();
    options.addOption(helpOpt);
    options.addOption(dimOpt);
    options.addOption(winLengthOpt);
    options.addOption(gameIdOpt);
    options.addOption(player1IdOpt);
    options.addOption(player2IdOpt);
    options.addOption(randomizeOpt);
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
      if (line.hasOption(gameIdOpt.getLongOpt())) {
        final String gameIdVal = line.getOptionValue(gameIdOpt.getLongOpt());
        try {
          gameId = Long.parseLong(gameIdVal);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse gameId: " + gameIdVal);
        }
      }
      if (line.hasOption(player1IdOpt.getLongOpt())) {
        final String player1IdVal = line.getOptionValue(player1IdOpt.getLongOpt());
        try {
          player1Id = Integer.parseInt(player1IdVal);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse player1Id: " + player1IdVal);
        }
      }
      if (line.hasOption(player2IdOpt.getLongOpt())) {
        final String player2IdVal = line.getOptionValue(player2IdOpt.getLongOpt());
        try {
          player2Id = Integer.parseInt(player2IdVal);
        }
        catch (final NumberFormatException e) {
          Logger.error(e, "could not parse player2Id: " + player2IdVal);
        }
      }
      randomize = line.hasOption(randomizeOpt.getLongOpt());
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
      playGame(dim, winLength, gameId, player1Id, player1Marker, player2Id, player2Marker, randomize, stateArgs);
    }
  }

  public static void playGame(final int dim, final int winLength, final long gameId, final int player1Id,
    final char player1Marker, final int player2Id, final char player2Marker, final boolean randomize,
    final String[] stateArgs) {
    final Player player1 = new Player(player1Id, player1Marker);
    final Player player2 = new Player(player2Id, player2Marker);
    final TicTacToeGame game = new TicTacToeGame(dim, winLength, gameId, player1, player2);
    if (stateArgs != null) {
      game.populate(stateArgs);
    }
    Logger.info("initial game state:\n{}", game.toString());
    Logger.info("is game over? {}", game.isGameOver());
    Logger.info("did any win?  {}", game.didAnyWin());
  }
}
