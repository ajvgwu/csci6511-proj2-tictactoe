package edu.gwu.ai.codeknights.tictactoe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import edu.gwu.ai.codeknights.tictactoe.filtering.chooser.AbstractCellChooser;
import edu.gwu.ai.codeknights.tictactoe.filtering.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.util.Const;

public class Main {

  public static void main(final String[] args) throws DimensionException, StateException, InterruptedException {
    // Default values
    boolean help = false;
    int dim = 3;
    int winLength = 3;
    long gameId = 1001;
    int player1Id = 1;
    final char player1Marker = Const.MASTER_PLAYER_CHAR;
    int player2Id = 2;
    final char player2Marker = Const.OPPONENT_PLAYER_CHAR;
    String[] stateArgs = null;
    String singlePlayChooser = null;
    String finishGameP1Chooser = null;
    String finishGameP2Chooser = null;
    String[] compareChoosers = null;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help")
      .desc("print this usage information")
      .build();
    final Option dimOpt = Option.builder("d").longOpt("dim")
      .hasArg().argName("DIM")
      .desc("board dimension (default is " + String.valueOf(dim) + ")")
      .build();
    final Option winLengthOpt = Option.builder("l").longOpt("win-length")
      .hasArg().argName("LEN")
      .desc("length of sequence required to win (default is " + String.valueOf(winLength) + ")")
      .build();
    final Option gameIdOpt = Option.builder().longOpt("game-id")
      .hasArg().argName("ID")
      .desc("identifier for game (default is " + String.valueOf(gameId) + ")")
      .build();
    final Option player1IdOpt = Option.builder().longOpt("player1-id")
      .hasArg().argName("ID")
      .desc("identifier for player 1 (default is " + String.valueOf(player1Id) + ")")
      .build();
    final Option player2IdOpt = Option.builder().longOpt("player2-id")
      .hasArg().argName("ID")
      .desc("identifier for player 2 (default is " + String.valueOf(player2Id) + ")")
      .build();
    final Option stateOpt = Option.builder("s").longOpt("state")
      .hasArgs().argName("CELLS")
      .desc("initial state of board (default is an empty board); moves of the first player given by \""
        + String.valueOf(player1Marker) + "\"; moves of the second player given by \"" + String.valueOf(player2Marker)
        + "\"; empty spaces given by any other string")
      .build();
    final Option singlePlayOpt = Option.builder().longOpt("single-play")
      .hasArg().argName("CHOOSER")
      .desc("choose the next play using the given strategy (one of " + String.valueOf(getChooserNames()) + ")")
      .build();
    final Option finishGameOpt = Option.builder().longOpt("finish-game")
      .numberOfArgs(2).argName("P1-CHOOSER P2-CHOOSER")
      .desc(
        "finish playing the game using the given strategies for player1 and player2, respectively (any combination of "
          + String.valueOf(getChooserNames()) + ")")
      .build();
    final Option compareChoosersOpt = Option.builder().longOpt("compare-choosers")
      .hasArgs().argName("CHOOSERS...")
      .desc("compare the given strategies for a single move (any of " + String.valueOf(getChooserNames()) + ")")
      .build();

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
    options.addOption(compareChoosersOpt);

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
          "choose only one of the following options: "
            + singlePlayOpt.getLongOpt() + ", "
            + finishGameOpt.getLongOpt());
      }
      singlePlayChooser = parseString(line, singlePlayOpt, singlePlayChooser);
      final String[] finishGameChoosers = parseStringArray(line, finishGameOpt, null);
      if (finishGameChoosers != null && finishGameChoosers.length > 0) {
        finishGameP1Chooser = finishGameChoosers[0];
        finishGameP2Chooser = finishGameChoosers.length > 1 ? finishGameChoosers[1] : null;
      }
      if ((singlePlayChooser != null || finishGameChoosers != null)
        && line.hasOption(compareChoosersOpt.getLongOpt())) {
        line = null;
        Logger.error(
          "cannot only one of the following options: "
            + singlePlayOpt.getLongOpt() + ", "
            + finishGameOpt.getLongOpt() + ", "
            + compareChoosersOpt.getLongOpt());
      }
      compareChoosers = parseStringArray(line, compareChoosersOpt, null);
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
      if (singlePlayChooser != null) {
        // Play the next move
        final AbstractCellChooser chooser = getChooserByName(singlePlayChooser);
        player1.setChooser(chooser);
        player2.setChooser(chooser);
        new TestScenario(game).singlePlay();
      }
      else if (finishGameP1Chooser != null || finishGameP2Chooser != null) {
        // Finish the current game
        player1.setChooser(getChooserByName(finishGameP1Chooser));
        player2.setChooser(getChooserByName(finishGameP2Chooser));
        new TestScenario(game).finishGame();
      }
      else if (compareChoosers != null) {
        // Compare performance for a list of choosers
        final Map<String, AbstractCellChooser> chooserMap = new LinkedHashMap<>();
        for (final String name : compareChoosers) {
          chooserMap.put(name, getChooserByName(name));
        }
        final Map<String, TestResult> resultMap = new LinkedHashMap<>();
        for (final String name : chooserMap.keySet()) {
          final AbstractCellChooser chooser = chooserMap.get(name);
          System.out.println("testing chooser '" + name + "' (class=" + chooser.getClass().getSimpleName() + ") ...");
          player1.setChooser(chooser);
          player2.setChooser(chooser);
          try {
            final TicTacToeGame copy = game.getCopy(game.getGameId(), player1, player2);
            final Player nextPlayer = copy.getNextPlayer();
            Integer rowIdx = null;
            Integer colIdx = null;
            String playerMark = null;
            final long startTimeMs = System.currentTimeMillis();
            final Cell cell = nextPlayer.chooseCell(copy);
            final long endTimeMs = System.currentTimeMillis();
            final double elapsedSec = (double) ((endTimeMs - startTimeMs) / 1000.0);
            if (cell != null) {
              rowIdx = cell.getRowIdx();
              colIdx = cell.getColIdx();
              copy.playInCell(rowIdx, colIdx, nextPlayer);
              playerMark = String.valueOf(copy.getBoard().getCell(rowIdx, colIdx).getPlayer().getMarker());
            }
            resultMap.put(name, new TestResult(rowIdx, colIdx, playerMark, elapsedSec));
            System.out.println("time elapsed (sec): " + String.valueOf(elapsedSec));
          }
          catch (final Exception e) {
            Logger.error(e, "error while testing chooser: " + name);
            resultMap.put(name, new TestResult());
          }
          System.out.println();
        }
        for (final String name : resultMap.keySet()) {
          final TestResult result = resultMap.get(name);
          System.out.println(name + ": " + result.toString());
        }
      }
      else {
        Logger.debug("no gameplay options were provided; will not play any moves");
        new TestScenario(game).printCurGameInfo();
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

  public static List<String> getChooserNames() {
    return Chooser.getNames();
  }

  public static AbstractCellChooser getChooserByName(String name) throws IllegalArgumentException {
    name = name != null ? name.trim() : "";
    final Chooser chooser = Chooser.fromName(name);
    if (chooser != null) {
      return chooser.createChooser();
    }
    throw new IllegalArgumentException("unknown chooser name: " + name);
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
      System.out.println("current game state:\n" + game.toString());
      final boolean isGameOver = game.isGameOver();
      final boolean didAnyWin = game.didAnyWin();
      final boolean didP1Win = game.didPlayer1Win();
      final boolean didP2Win = game.didPlayer2Win();
      System.out.println("is game over? " + String.valueOf(isGameOver));
      if (isGameOver) {
        System.out.println("did any win?  " + String.valueOf(didAnyWin));
        if (didAnyWin) {
          System.out.println("did P1 win?   " + String.valueOf(didP1Win));
          System.out.println("did P2 win?   " + String.valueOf(didP2Win));
        }
      }
    }

    public void singlePlay() {
      printCurGameInfo();
      if (!game.isGameOver()) {
        try {
          game.tryPlayNextCell();
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
          game.tryPlayNextCell();
          printCurGameInfo();
        }
      }
      catch (final GameException e) {
        Logger.error(e, "could not finish game");
      }
    }
  }

  public static class TestResult {

    public final Integer rowIdx;
    public final Integer colIdx;
    public final String playerMark;
    public final Double elapsedSec;

    public TestResult(final Integer rowIdx, final Integer colIdx, final String playerMark, final double elapsedSec) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.playerMark = playerMark;
      this.elapsedSec = elapsedSec;
    }

    public TestResult() {
      rowIdx = null;
      colIdx = null;
      playerMark = null;
      elapsedSec = null;
    }

    @Override
    public String toString() {
      return new StringBuilder()
        .append("rowIdx=").append(rowIdx).append(", ")
        .append("colIdx=").append(colIdx).append(", ")
        .append("playerMark=").append(playerMark).append(", ")
        .append("elapsedSec=").append(elapsedSec).append(", ")
        .toString();
    }
  }
}
