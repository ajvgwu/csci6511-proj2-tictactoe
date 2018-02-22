package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Move;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class ParallelAlphaBetaPruningChooser extends AlphaBetaPruningChooserAI {

  @Override
  public Move findNextMove(final Game game) {
    if (game.isGameOver()) {
      return null;
    }
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = game.getNextPlayer();
    if (numFirst + numOther == 0) {
      final int dim = game.getRowLen();
      final int center = (int) (dim / 2);
      return new Move(center, center, curPlayer, null);
    }
    final List<Move> moves = findPossibleMoves(game);
    final List<AbpThread> threads = new ArrayList<>();
    for (final Move move : moves) {
      final Game newGame = game.getCopy();
      final AbpThread t = new AbpThread(curPlayer, move, newGame);
      threads.add(t);
      t.start();
    }
    Long bestScore = null;
    final List<Move> bestMoves = new ArrayList<>();
    for (final AbpThread t : threads) {
      try {
        t.join();
        final Move move = t.getMove();
        final Long score = move.getScore();
        if (bestScore == null || score > bestScore) {
          bestScore = score;
          bestMoves.clear();
          bestMoves.add(move);
        }
        else if (score.equals(bestScore)) {
          bestMoves.add(move);
        }
      }
      catch (final InterruptedException e) {
        Logger.error(e, "interrupted while joining thread");
      }
    }

    // Return result
    return selectMove(game, bestMoves);
  }

  private class AbpThread extends Thread {

    private final int curPlayer;
    private final Move move;
    private final Game newGame;

    private Long score;

    public AbpThread(final int curPlayer, final Move move, final Game newGame) {
      this.curPlayer = curPlayer;
      this.move = move;
      this.newGame = newGame;

      score = 0L;
    }

    @Override
    public void run() {
      newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
      score = alphabetapruning(newGame, curPlayer,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
      move.setScore(score);
    }

    public Move getMove() {
      return move;
    }
  }
}
