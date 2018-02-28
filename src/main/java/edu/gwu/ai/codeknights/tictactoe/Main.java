package edu.gwu.ai.codeknights.tictactoe;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import edu.gwu.ai.codeknights.tictactoe.selector.PlayChooser;
import edu.gwu.ai.codeknights.tictactoe.selector.Player;
import edu.gwu.ai.codeknights.tictactoe.selector.RandomChooser;
import edu.gwu.ai.codeknights.tictactoe.selector.RuleBasedChooser;
import edu.gwu.ai.codeknights.tictactoe.selector.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.util.Const;

public class Main {

  public static void main(final String[] args) throws DimensionException, StateException, InterruptedException {
    // Default values
    boolean help = false;
    int dim = 3;
    int winLength = 3;
    long gameId = 100;
    int player1Id = 1;
    final char player1Marker = Const.MASTER_PLAYER_CHAR;
    int player2Id = 2;
    final char player2Marker = Const.OPPONENT_PLAYER_CHAR;
    String[] stateArgs = null;
    String singlePlayStrategy = null;
    String finishGameP1Strategy = null;
    String finishGameP2Strategy = null;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help").desc("print this usage information").build();
    final Option dimOpt = Option.builder("d").longOpt("dim").hasArg().argName("DIM")
      .desc("board dimension (default is " + String.valueOf(dim) + ")").build();
    final Option winLengthOpt = Option.builder("l").longOpt("win-length").hasArg().argName("LEN")
      .desc("length of sequence required to win (default is " + String.valueOf(winLength) + ")").build();
    final Option gameIdOpt = Option.builder().longOpt("game-id").hasArg().argName("ID")
      .desc("identifier for game (default is " + String.valueOf(gameId) + ")").build();
    final Option player1IdOpt = Option.builder().longOpt("player1-id").hasArg().argName("ID")
      .desc("identifier for player 1 (default is " + String.valueOf(player1Id) + ")").build();
    final Option player2IdOpt = Option.builder().longOpt("player2-id").hasArg().argName("ID")
      .desc("identifier for player 2 (default is " + String.valueOf(player2Id) + ")").build();
    final Option stateOpt = Option.builder("s").longOpt("state").hasArgs().argName("CELLS")
      .desc("initial state of board (default is an empty board); moves of the first player given by \""
        + String.valueOf(player1Marker) + "\"; moves of the second player given by \"" + String.valueOf(player2Marker)
        + "\"; empty spaces given by any other string")
      .build();
    final Option singlePlayOpt = Option.builder().longOpt("single-play").hasArg().argName("STRATEGY")
      .desc("choose the next play using the given strategy (one of " + String.valueOf(getPlayChooserNames()) + ")")
      .build();
    final Option finishGameOpt = Option.builder().longOpt("finish-game").numberOfArgs(2).argName("P1-P2-STRATEGIES")
      .desc(
        "finish playing the game using the given strategies for player1 and player2, respectively (any combination of "
          + String.valueOf(getPlayChooserNames()) + ")")
      .build();

    // TODO: implement quick tests for the CellFilter / CellSelector & PlayChooser functionality
    // TODO: e.g., for board 'X O X O X O . . .', what does a given filter/chooser do ???

    final Options options = new Options();
    options.addOption(helpOpt);
    options.addOption(dimOpt);
    options.addOption(winLengthOpt);
    options.addOption(gameIdOpt);
    options.addOption(player1IdOpt);
    options.addOption(player2IdOpt);
    options.addOption(stateOpt);
    options.addOption(singlePlayOpt);
    options.addOption(finishGameOpt);

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
      help = parseBoolean(line, helpOpt);
      dim = parseInt(line, dimOpt, dim);
      winLength = parseInt(line, winLengthOpt, winLength);
      gameId = parseLong(line, gameIdOpt, gameId);
      player1Id = parseInt(line, player1IdOpt, player1Id);
      player2Id = parseInt(line, player2IdOpt, player2Id);
      stateArgs = parseStringArray(line, stateOpt, stateArgs);
      if (line.hasOption(singlePlayOpt.getLongOpt()) && line.hasOption(finishGameOpt.getLongOpt())) {
        line = null;
        Logger.error(
          "cannot choose both of these options: " + singlePlayOpt.getLongOpt() + ", " + finishGameOpt.getLongOpt());
      }
      singlePlayStrategy = parseString(line, singlePlayOpt, singlePlayStrategy);
      final String[] finishGameStrategies = parseStringArray(line, finishGameOpt, null);
      if (finishGameStrategies != null && finishGameStrategies.length > 0) {
        finishGameP1Strategy = finishGameStrategies[0];
        finishGameP2Strategy = finishGameStrategies.length > 1 ? finishGameStrategies[1] : null;
      }
    }
    if (line == null || help) {
      // Print usage information
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(" ", options);
      if (line == null) {
        // Parse error, so non-zero exit code
        System.exit(1);
      }
    }
    else {
      // Create the game
      final Player player1 = new Player(player1Id, player1Marker);
      final Player player2 = new Player(player2Id, player2Marker);
      final TicTacToeGame game = new TicTacToeGame(dim, winLength, gameId, player1, player2);
      if (stateArgs != null) {
        game.populate(stateArgs);
      }
      final TestScenario scenario = new TestScenario(game);
      if (singlePlayStrategy != null) {
        final PlayChooser chooser = getPlayChooserByName(singlePlayStrategy);
        player1.setChooser(chooser);
        player2.setChooser(chooser);
        scenario.singlePlay();
      }
      else if (finishGameP1Strategy != null || finishGameP2Strategy != null) {
        player1.setChooser(getPlayChooserByName(finishGameP1Strategy));
        player2.setChooser(getPlayChooserByName(finishGameP2Strategy));
        scenario.finishGame();
      }
      else {
        Logger.debug("neither option " + singlePlayOpt.getLongOpt() + " nor " + finishGameOpt.getLongOpt()
          + " was provided; will not play any moves");
        scenario.printCurGameInfo();
      }
    }
  }

  public static boolean parseBoolean(final CommandLine line, final Option opt) {
    return line.hasOption(opt.getOpt());
  }

  public static int parseInt(final CommandLine line, final Option opt, final int defaultVal) {
    final String optName = opt.getLongOpt();
    if (line.hasOption(optName)) {
      final String val = line.getOptionValue(optName);
      try {
        return Integer.parseInt(val);
      }
      catch (final NumberFormatException e) {
        Logger.error(e, "could not parse " + optName + " as int: " + val);
      }
    }
    return defaultVal;
  }

  public static long parseLong(final CommandLine line, final Option opt, final long defaultVal) {
    final String optName = opt.getLongOpt();
    if (line.hasOption(optName)) {
      final String val = line.getOptionValue(optName);
      try {
        return Long.parseLong(val);
      }
      catch (final NumberFormatException e) {
        Logger.error(e, "could not parse " + optName + " as long: " + val);
      }
    }
    return defaultVal;
  }

  public static String parseString(final CommandLine line, final Option opt, final String defaultVal) {
    final String optName = opt.getLongOpt();
    if (line.hasOption(optName)) {
      final String val = line.getOptionValue(optName);
      return val;
    }
    return defaultVal;
  }

  public static String[] parseStringArray(final CommandLine line, final Option opt, final String[] defaultVal) {
    final String optName = opt.getLongOpt();
    if (line.hasOption(optName)) {
      final String[] valArray = line.getOptionValues(optName);
      return valArray;
    }
    return defaultVal;
  }

  public static List<String> getPlayChooserNames() {
    return Arrays.asList(RuleBasedChooser.NAME, RandomChooser.NAME);
  }

  public static PlayChooser getPlayChooserByName(String name) throws IllegalArgumentException {
    name = name != null ? name.trim() : "";
    switch (name) {
      case RuleBasedChooser.NAME: {
        return new RuleBasedChooser();
      }
      case RandomChooser.NAME: {
        return new RandomChooser();
      }
      // TODO: add more PlayChoosers
      default: {
        throw new IllegalArgumentException("unknown chooser name: " + name);
      }
    }
  }

  public static class TestScenario {

    private final TicTacToeGame game;

    public TestScenario(final TicTacToeGame game) {
      this.game = game;
    }

    public TicTacToeGame getGame() {
      return game;
    }

    public void printCurGameInfo() {
      Logger.info("current game state:\n{}", game.toString());
      final boolean isGameOver = game.isGameOver();
      final boolean didAnyWin = game.didAnyWin();
      final boolean didP1Win = game.didPlayer1Win();
      final boolean didP2Win = game.didPlayer2Win();
      Logger.info("is game over? {}", isGameOver);
      if (isGameOver) {
        Logger.info("did any win?  {}", didAnyWin);
        if (didAnyWin) {
          Logger.info("did P1 win?   {}", didP1Win);
          Logger.info("did P2 win?   {}", didP2Win);
        }
      }
    }

    public void singlePlay() {
      printCurGameInfo();
      if (!game.isGameOver()) {
        try {
          game.tryPlayNextMove();
          printCurGameInfo();
        }
        catch (final GameException e) {
          Logger.error(e, "could not play a move");
        }
      }
    }

    public void finishGame() {
      printCurGameInfo();
      try {
        while (!game.isGameOver()) {
          game.tryPlayNextMove();
          printCurGameInfo();
        }
      }
      catch (final GameException e) {
        Logger.error(e, "could not finish game");
      }
    }
  }
}
