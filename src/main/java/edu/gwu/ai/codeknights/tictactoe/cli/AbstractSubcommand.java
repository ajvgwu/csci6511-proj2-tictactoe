package edu.gwu.ai.codeknights.tictactoe.cli;

import java.util.concurrent.Callable;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.core.exception.GameException;
import edu.gwu.ai.codeknights.tictactoe.util.Const;
import picocli.CommandLine.Option;

public abstract class AbstractSubcommand implements Callable<Void> {

  @Option(
    names = {"-h", "--help"}, usageHelp = true,
    description = "show this help message and exit")
  private boolean help = false;

  @Option(
    names = {"-d", "--dim"},
    description = "board dimension")
  private int dim = 6;

  @Option(
    names = {"-l", "--win-length"},
    description = "length of sequence required to win")
  private int winLength = 4;

  @Option(
    names = {"--game-id"},
    description = "identifier for game")
  private long gameId = 0;

  @Option(
    names = {"--player1-id"},
    description = "identifier for player1")
  private int player1Id = 1;

  @Option(
    names = {"--player2-id"},
    description = "identifier for player2")
  private int player2Id = 2;

  protected void validateArgs() throws Exception {
    help = !!help;
    dim = Math.max(0, dim);
    if (dim < 1) {
      throw new IllegalArgumentException("dim must be >= 1");
    }
    winLength = Math.max(0, winLength);
    if (winLength < 1 || winLength > dim) {
      throw new IllegalArgumentException("winLength must be >= 1 and <= dim");
    }
    gameId = Math.max(0, gameId);
    player1Id = Math.max(0, player1Id);
    player2Id = Math.max(0, player2Id);
  }

  protected boolean isHelp() {
    return help;
  }

  protected int getDim() {
    return dim;
  }

  protected int getWinLength() {
    return winLength;
  }

  protected long getGameId() {
    return gameId;
  }

  protected int getPlayer1Id() {
    return player1Id;
  }

  protected int getPlayer2Id() {
    return player2Id;
  }

  protected Game createGame(boolean isHome) {
    final Player player1 = new Player(getPlayer1Id(), Const.MASTER_PLAYER_CHAR);
    final Player player2 = new Player(getPlayer2Id(), Const.OPPONENT_PLAYER_CHAR);
    return new Game(getDim(), getWinLength(), getGameId(), player1,
            player2, isHome);
  }

  @Override
  public abstract Void call() throws Exception;

  public static class GameplayHelper {

    private final Game game;

    public GameplayHelper(final Game game) {
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

    public void singleMove() {
      printCurGameInfo();
      try {
        if (!game.isGameOver()) {
          final long startTimeMs = System.currentTimeMillis();
          game.tryPlayNextCell();
          final long endTimeMs = System.currentTimeMillis();
          final double elapsedSec = (double) ((endTimeMs - startTimeMs) / 1000.0);
          System.out.println("time elapsed: " + String.valueOf(elapsedSec));
          printCurGameInfo();
        }
      }
      catch (final GameException e) {
        Logger.error(e, "could not play a move");
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
}
