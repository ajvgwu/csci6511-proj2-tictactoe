package edu.gwu.ai.codeknights.tictactoe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractCellChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.Chooser;
import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.Filter;
import edu.gwu.ai.codeknights.tictactoe.gui.TicTacToe;
import edu.gwu.ai.codeknights.tictactoe.util.Const;

public class Main {

  public static void main(final String[] args) {
    // Default values
    boolean help = false;
    boolean gui = false;
    int dim = 3;
    int winLength = 3;
    long gameId = 1001;
    int player1Id = 1;
    final char player1Marker = Const.MASTER_PLAYER_CHAR;
    int player2Id = 2;
    final char player2Marker = Const.OPPONENT_PLAYER_CHAR;
    String[] stateArgs = null;
    String fallbackChooser = Chooser.MAX_UTILITY.getName();
    String singlePlayChooser = null;
    String finishGameP1Chooser = null;
    String finishGameP2Chooser = null;
    String[] compareChoosers = null;
    String testFilter = null;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help")
      .desc("print this usage information")
      .build();
    final Option guiOpt = Option.builder().longOpt("gui")
      .desc("launch in GUI mode")
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
      .desc("identifier for player1 (default is " + String.valueOf(player1Id) + ")")
      .build();
    final Option player2IdOpt = Option.builder().longOpt("player2-id")
      .hasArg().argName("ID")
      .desc("identifier for player2 (default is " + String.valueOf(player2Id) + ")")
      .build();
    final Option stateOpt = Option.builder("s").longOpt("state")
      .hasArgs().argName("CELLS")
      .desc("initial state of board (default is an empty board); moves of the first player given by \""
        + String.valueOf(player1Marker) + "\"; moves of the second player given by \"" + String.valueOf(player2Marker)
        + "\"; empty spaces given by any other string")
      .build();
    final Option fallbackChooserOpt = Option.builder().longOpt("fallback-chooser")
      .hasArg().argName("CHOOSER")
      .desc("strategy used by every player to select a cell when their primary strategy does not return a result"
        + " (default is \"" + fallbackChooser + "\")")
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
      .hasArgs().argName("CHOOSER [CHOOSER...]")
      .desc("compare the given strategies for a single move (any of " + String.valueOf(getChooserNames()) + ")")
      .build();
    final Option testFilterOpt = Option.builder().longOpt("test-filter")
      .hasArg().argName("FILTER")
      .desc("test the given filter (any one of " + String.valueOf(getFilterNames()) + ")")
      .build();

    final Options options = new Options();
    options.addOption(helpOpt);
    options.addOption(guiOpt);
    options.addOption(dimOpt);
    options.addOption(winLengthOpt);
    options.addOption(gameIdOpt);
    options.addOption(player1IdOpt);
    options.addOption(player2IdOpt);
    options.addOption(stateOpt);
    options.addOption(fallbackChooserOpt);
    options.addOption(singlePlayOpt);
    options.addOption(finishGameOpt);
    options.addOption(compareChoosersOpt);
    options.addOption(testFilterOpt);

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
      gui = parseBoolean(line, guiOpt);
      dim = parseInt(line, dimOpt, dim);
      winLength = parseInt(line, winLengthOpt, winLength);
      gameId = parseLong(line, gameIdOpt, gameId);
      player1Id = parseInt(line, player1IdOpt, player1Id);
      player2Id = parseInt(line, player2IdOpt, player2Id);
      stateArgs = parseStringArray(line, stateOpt, stateArgs);
      fallbackChooser = parseString(line, fallbackChooserOpt, fallbackChooser);
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
          "choose only one of the following options: "
            + singlePlayOpt.getLongOpt() + ", "
            + finishGameOpt.getLongOpt() + ", "
            + compareChoosersOpt.getLongOpt());
      }
      compareChoosers = parseStringArray(line, compareChoosersOpt, null);
      if ((singlePlayChooser != null || finishGameChoosers != null || compareChoosers != null)
        && line.hasOption(testFilterOpt.getLongOpt())) {
        line = null;
        Logger.error(
          "choose only one of the following options: "
            + singlePlayOpt.getLongOpt() + ", "
            + finishGameOpt.getLongOpt() + ", "
            + compareChoosersOpt.getLongOpt() + ", "
            + testFilterOpt.getLongOpt());
      }
      testFilter = parseString(line, testFilterOpt, null);
    }
    if (line == null || help) {
      // Print usage information
      final HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(100);
      formatter.printHelp(" ", options);
      if (line == null) {
        // Parse error, so non-zero exit code
        System.exit(1);
      }
    }
    else if (gui) {
      // Launch GUI
      TicTacToe.main(args);
    }
    else {
      // Create the game
      final Player player1 = new Player(player1Id, player1Marker);
      final Player player2 = new Player(player2Id, player2Marker);
      final Game game = new Game(dim, winLength, gameId, player1, player2);
      if (stateArgs != null) {
        game.populate(stateArgs);
      }
      if (fallbackChooser != null) {
        player1.setFallbackChooser(getChooserByName(fallbackChooser));
        player2.setFallbackChooser(getChooserByName(fallbackChooser));
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
      else if (testFilter != null) {
        // Test the given filter
        final TestScenario scenario = new TestScenario(game);
        System.out.println("### Initial state ###");
        scenario.printCurGameInfo();
        final AbstractCellFilter filter = getFilterByName(testFilter);
        final List<Cell> candidates = filter.filterCells(game).collect(Collectors.toList());
        final Player blankMarker = new Player(0, ' ');
        final Player candidateMarker = new Player(0, '?');
        for (final Cell cell : game.getBoard().getAllCells()) {
          if (cell.isEmpty()) {
            cell.setPlayer(blankMarker);
          }
        }
        for (final Cell cell : candidates) {
          cell.setPlayer(candidateMarker);
        }
        System.out.println();
        System.out.println("### Showing candidates as '" + String.valueOf(candidateMarker.getMarker()) + "' ###");
        scenario.printCurGameInfo();
      }
      else if (compareChoosers != null) {
        // Compare performance for a list of choosers
        final Map<String, AbstractCellChooser> chooserMap = new LinkedHashMap<>();
        for (final String name : compareChoosers) {
          chooserMap.put(name, getChooserByName(name));
        }
        System.out.println(
          "will test " + String.valueOf(chooserMap.size()) + " choosers on this board:\n" + String.valueOf(game));
        final Map<String, TestResult> resultMap = new LinkedHashMap<>();
        for (final String name : chooserMap.keySet()) {
          final AbstractCellChooser chooser = chooserMap.get(name);
          System.out.println("testing chooser '" + name + "' (class=" + chooser.getClass().getSimpleName() + ") ...");
          player1.setChooser(chooser);
          player2.setChooser(chooser);
          try {
            final Game copy = game.getCopy(game.getGameId(), player1, player2);
            final Player nextPlayer = copy.getNextPlayer();
            Integer rowIdx = null;
            Integer colIdx = null;
            String playerMark = null;
            Long playerUtility = null;
            final long startTimeMs = System.currentTimeMillis();
            final Cell cell = nextPlayer.chooseCell(copy);
            final long endTimeMs = System.currentTimeMillis();
            final double elapsedSec = (double) ((endTimeMs - startTimeMs) / 1000.0);
            if (cell != null) {
              rowIdx = cell.getRowIdx();
              colIdx = cell.getColIdx();
              copy.playInCell(rowIdx, colIdx, nextPlayer);
              playerUtility = copy.evaluatePlayerUtility(nextPlayer);
              playerMark = String.valueOf(copy.getBoard().getCell(rowIdx, colIdx).getPlayer().getMarker());
            }
            System.out.println("updated board:");
            System.out.println(String.valueOf(copy));
            resultMap.put(name, new TestResult(rowIdx, colIdx, playerMark, playerUtility, elapsedSec));
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
    return line.hasOption(opt.getLongOpt());
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

  public static List<String> getFilterNames() {
    return Filter.getNames();
  }

  public static AbstractCellFilter getFilterByName(String name) throws IllegalArgumentException {
    name = name != null ? name.trim() : "";
    final Filter filter = Filter.fromName(name);
    if (filter != null) {
      return filter.createFilter();
    }
    throw new IllegalArgumentException("unknown filter name: " + name);
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

    private final Game game;

    public TestScenario(final Game game) {
      this.game = game;
    }

    public Game getGame() {
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
          final long startTimeMs = System.currentTimeMillis();
          game.tryPlayNextCell();
          final long endTimeMs = System.currentTimeMillis();
          final double elapsedSec = (double) ((endTimeMs - startTimeMs) / 1000.0);
          System.out.println("time elapsed: " + String.valueOf(elapsedSec));
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
    public final Long playerUtility;
    public final Double elapsedSec;

    public TestResult(final Integer rowIdx, final Integer colIdx, final String playerMark, final Long playerUtility,
      final double elapsedSec) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.playerMark = playerMark;
      this.playerUtility = playerUtility;
      this.elapsedSec = elapsedSec;
    }

    public TestResult() {
      rowIdx = null;
      colIdx = null;
      playerMark = null;
      playerUtility = null;
      elapsedSec = null;
    }

    @Override
    public String toString() {
      return new StringBuilder()
        .append("rowIdx=").append(rowIdx).append(", ")
        .append("colIdx=").append(colIdx).append(", ")
        .append("playerMark=").append(playerMark).append(", ")
        .append("playerUtility=").append(playerUtility).append(", ")
        .append("elapsedSec=").append(elapsedSec)
        .toString();
    }
  }
}
