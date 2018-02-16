package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

public class ParallelMoveChooser extends MoveChooser {

  @Override
  public Move findBestMove(final Game game) {
    if (game.isGameOver()) {
      return null;
    }
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = game.getNextPlayer();
    if (numFirst + numOther == 0) {
      final int dim = game.getDim();
      final int center = (int) (dim / 2);
      return new Move(center, center, curPlayer, null);
    }
    final List<Move> moves = findPossibleMoves(game);
    final List<MinimaxThread> threads = new ArrayList<>();
    for (final Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        final MinimaxThread t = new MinimaxThread(curPlayer, move, newGame);
        threads.add(t);
        t.start();
      }
      catch (DimensionException | StateException e) {
        Logger.error(e, "could not copy game state");
      }
    }
    Integer bestScore = null;
    final List<Move> bestMoves = new ArrayList<>();
    for (final MinimaxThread t : threads) {
      try {
        t.join();
        final Move move = t.getMove();
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
    return getMoveOrNull(bestMoves);
  }

  private class MinimaxThread extends Thread {

    private final int curPlayer;
    private final Move move;
    private final Game newGame;

    private int score;

    public MinimaxThread(final int curPlayer, final Move move, final Game newGame) {
      this.curPlayer = curPlayer;
      this.move = move;
      this.newGame = newGame;

      score = 0;
    }

    @Override
    public void run() {
      newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
      score = minimax(newGame, curPlayer, 0);
      move.setScore(score);
    }

    public Move getMove() {
      return move;
    }
  }
}
