package edu.gwu.ai.codeknights.tictactoe;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class ParallelAlphaBetaPruningChooser extends AlphaBetaPruningChooser {

  @Override
  public Game.Move findBestMove(final Game game) {
    if (game.isGameOver()) {
      return null;
    }
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = game.getNextPlayer();
    if (numFirst + numOther == 0) {
      final int dim = game.getDim();
      final int center = (int) (dim / 2);
      return new Game.Move(center, center, curPlayer, null);
    }
    final List<Game.Move> moves = findPossibleMoves(game);
    final List<AbpThread> threads = new ArrayList<>();
    for (final Game.Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        final AbpThread t = new AbpThread(curPlayer, move, newGame);
        threads.add(t);
        t.start();
      }
      catch (DimensionException | StateException e) {
        Logger.error(e, "could not copy game state");
      }
    }
    Integer bestScore = null;
    final List<Game.Move> bestMoves = new ArrayList<>();
    for (final AbpThread t : threads) {
      try {
        t.join();
        final Game.Move move = t.getMove();
        final int score = move.getScore();
        if (bestScore == null || score > bestScore) {
          bestScore = score;
          bestMoves.clear();
          bestMoves.add(move);
        }
        else if (bestScore != null && score == bestScore) {
          bestMoves.add(move);
        }
      }
      catch (final InterruptedException e) {
        Logger.error(e, "interrupted while joining thread");
      }
    }

    // Return result
    return selectMove(bestMoves);
  }

  private class AbpThread extends Thread {

    private final int curPlayer;
    private final Game.Move move;
    private final Game newGame;

    private int score;

    public AbpThread(final int curPlayer, final Game.Move move, final Game newGame) {
      this.curPlayer = curPlayer;
      this.move = move;
      this.newGame = newGame;

      score = 0;
    }

    @Override
    public void run() {
      newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
      score = alphabetapruning(newGame, curPlayer,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
      move.setScore(score);
    }

    public Game.Move getMove() {
      return move;
    }
  }
}
