package edu.gwu.ai.codeknights.tictactoe;

import edu.gwu.ai.codeknights.tictactoe.chooser.AlphaBetaPruningChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.AIMoveChooser;
import edu.gwu.ai.codeknights.tictactoe.chooser.ParallelAlphaBetaPruningChooser;
import edu.gwu.ai.codeknights.tictactoe.core.Move;
import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import edu.gwu.ai.codeknights.tictactoe.gui.util.Player;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pmw.tinylog.Logger;

import java.util.Random;

public class Main {

  /*
   * TODO: Some things to consider:
   *   - alpha beta pruning algorithm?
   *   - better way of hashing? consider rotations/reflections/etc.?
   *   - pre-compute some lookup tables for various board sizes?
   *   - how to make best-effort choice if we are running out of time?
   *   - is there a general heuristic (for any board size) to choose the BEST FIRST MOVE? i.e., always play close to center?
   *   - what is the API to interface w/ the REST/JSON game server?
   */

  public static void main(final String[] args) throws DimensionException, StateException, InterruptedException {
    // Default values
    int dim = 5;
    int winLength = 4;
    String[] stateArgs = null;
    long gameId = new Random().nextInt(1000);
    int masterId = 10;
    int opId = 20;

    // Command-line options
    final Option helpOpt = Option.builder("h").longOpt("help").desc("print this usage information").build();
    final Option dimOpt = Option.builder("d").longOpt("dim").hasArg().argName("DIM")
      .desc("board dimension (default is " + String.valueOf(dim) + ")").build();
    final Option winLengthOpt = Option.builder("l").longOpt("win-length").hasArg().argName("LEN")
      .desc("length of sequence required to win (default is " + String.valueOf(winLength) + ")").build();
    final Option stateOpt = Option.builder("s").longOpt("state").hasArgs().argName("CELLS")
      .desc("initial state of board (default is an empty board); moves of the first player given by '"
        + String.valueOf(Const.MASTER_PLAYER_CHAR) + "' or '" + String.valueOf(masterId)
        + "'; moves of the other player given by '" + String.valueOf(Const.OPPONENT_PLAYER_CHAR) + "' or '"
        + String.valueOf(opId) + "'; empty spaces given by '" + String.valueOf(Const.BLANK_SPACE_CHAR)
        + "'")
      .build();
    final Option randomizeOpt = Option.builder("r").longOpt("randomize")
      .desc("when multiple moves are scored equally, randomly choose from among them").build();
    final Option testOpt = Option.builder("t").longOpt("test").desc("run performance test").build();

    final Options options = new Options();
    options.addOption(helpOpt);
    options.addOption(dimOpt);
    options.addOption(winLengthOpt);
    options.addOption(stateOpt);
    options.addOption(randomizeOpt);
    options.addOption(testOpt);

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
              if (curArg.equalsIgnoreCase(String.valueOf(Const.MASTER_PLAYER_CHAR))) {
                board[i][j] = masterId;
              }
              else if (curArg.equalsIgnoreCase(String.valueOf(Const.OPPONENT_PLAYER_CHAR))) {
                board[i][j] = opId;
              }
              else {
                board[i][j] = null;
              }
            }
          }
        }
      }
      final Game game = new Game(gameId, dim, winLength, board, masterId, opId);
      final boolean randomize = line.hasOption(randomizeOpt.getLongOpt());

      if (line.hasOption(testOpt.getLongOpt())) {
        // Run performance test
        runPerformanceTest(game, randomize);
      }
      else {
        // Play a complete game, starting from the current state
        playGame(game, randomize);
      }
    }
  }

  public static void runPerformanceTest(final Game game, final boolean randomize)
    throws DimensionException, StateException, InterruptedException {
    Logger.info("dim={}, winLength={}, hash={}", game.getDim(), game.getWinLength(), game.getBoardHash());
    Logger.info("All lines on board:\n{}\n", game.toStringAllLines(" * "));
    Logger.info("Game board state:\n{}\n", game.toString());
    Logger.info("# spaces:       {}={}, {}={}, {}={}", Const.MASTER_PLAYER_CHAR, game.countFirstPlayer(),
      Const.OPPONENT_PLAYER_CHAR, game.countOtherPlayer(), Const.BLANK_SPACE_CHAR, game.countEmpty());
    boolean isGameOver = game.isGameOver();
    Logger.info("Is game over?   {}", isGameOver);
    Game curGame = game;
    int moveIdx = 0;
    while (!isGameOver) {
      moveIdx++;
      Logger.info("  Move # {}", moveIdx);
      Game gameCopy = null;
      Logger.info("    - Testing parallel alpha-beta pruning algorithm " +
              "performance...");
      for (int i = 0; i < 3; i++) {
        gameCopy = curGame.getCopy();
        final AIMoveChooser moveChooser = new ParallelAlphaBetaPruningChooser();
        moveChooser.setRandomChoice(randomize);
        final long startMs = System.currentTimeMillis();
        final Move move = moveChooser.findNextMove(gameCopy);
        final long endMs = System.currentTimeMillis();
        final double timeSec = (double) (endMs - startMs) / 1000.0;
        Logger.info("      * Found move in {} sec: {}", timeSec, move.toString());
        gameCopy.setCellValue(move.rowIdx, move.colIdx, game.getNextPlayer());
      }
      curGame = gameCopy;
      isGameOver = curGame.isGameOver();
      Logger.info("Is game over?   {}", isGameOver);
    }
    Logger.info("Did anyone win? {}", curGame.didAnyPlayerWin());
    Logger.info("Who won?        {}={}, {}={}", Const.MASTER_PLAYER_CHAR, curGame.didFirstPlayerWin(),
      Const.OPPONENT_PLAYER_CHAR, curGame.didOtherPlayerWin());
  }

  public static void playGame(final Game game, final boolean randomize)
    throws DimensionException, StateException, InterruptedException {
    Logger.info("dim={}, winLength={}, hash={}", game.getDim(), game.getWinLength(), game.getBoardHash());
    Logger.info("All lines on board:\n{}\n", game.toStringAllLines(" * "));
    Logger.info("Game board state:\n{}\n", game.toString());
    Logger.info("# spaces:       {}={}, {}={}, {}={}", Const.MASTER_PLAYER_CHAR, game.countFirstPlayer(),
      Const.OPPONENT_PLAYER_CHAR, game.countOtherPlayer(), Const.BLANK_SPACE_CHAR, game.countEmpty());
    boolean isGameOver = game.isGameOver();
    Logger.info("Is game over?   {}", isGameOver);
    final AIMoveChooser AIMoveChooser1 = new ParallelAlphaBetaPruningChooser();
    final AIMoveChooser AIMoveChooser2 = new ParallelAlphaBetaPruningChooser();
    Player master = new Player(10, Const.PLAYER_SYMBOL_MASTER, AIMoveChooser1);

    while (!isGameOver) {
      final AIMoveChooser AIMoveChooser = new ParallelAlphaBetaPruningChooser();
      AIMoveChooser.setRandomChoice(randomize);
      final long startMs = System.currentTimeMillis();
      final Move bestMove = AIMoveChooser.findNextMove(game);
      game.setCellValue(bestMove.rowIdx, bestMove.colIdx, game.getNextPlayer());
      final long endMs = System.currentTimeMillis();
      final double timeSec = (double) (endMs - startMs) / 1000.0;
      Logger.info("Found best move in {} sec: {}\n{}\n", timeSec, bestMove.toString(), game.toString());
      isGameOver = game.isGameOver();
      Logger.info("Is game over?   {}", isGameOver);
    }
    Logger.info("Did anyone win? {}", game.didAnyPlayerWin());
    Logger.info("Who won?        {}={}, {}={}", Const.MASTER_PLAYER_CHAR, game.didFirstPlayerWin(),
      Const.OPPONENT_PLAYER_CHAR, game.didOtherPlayerWin());
  }
}
