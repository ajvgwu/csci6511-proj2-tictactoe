package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        final int score = t.getScore();
        if (bestScore == null || score > bestScore) {
          bestScore = score;
          bestMoves.clear();
          bestMoves.add(t.getMove());
        }
        else if (bestScore != null && score == bestScore) {
          bestMoves.add(t.getMove());
        }
      }
      catch (final InterruptedException e) {
        Logger.error("interrupted while joining thread", e);
      }
    }

    Move bestMove = null;
    if (bestMoves.size() > 1) {
      // If many moves scored equally, choose randomly from among them
      bestMove = bestMoves.get(new Random().nextInt(bestMoves.size()));
    }
    else if (bestMoves.size() > 0) {
      // Found a single best move
      bestMove = bestMoves.get(0);
    }
    if (bestMove != null) {
      bestMove.setScore(bestScore);
    }
    return bestMove;
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
    }

    public Move getMove() {
      return move;
    }

    public int getScore() {
      return score;
    }
  }
}
