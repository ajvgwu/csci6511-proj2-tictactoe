package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

public class MinimaxChooser extends MoveChooser {

  protected int minimax(final Game game, final int player, final int level) {
    // Check for terminal state
    if (game.isGameOver()) {
      // return game.getScore(player);
      if (game.didPlayerWin(player)) {
        return 10 - level;
      }
      else if (game.didAnyPlayerWin()) {
        return -10 + level;
      }
      return 0;
    }

    // Check if we've already solved this state
    synchronized (getHashScoreMap()) {
      final long hash = game.getBoardHash();
      final Integer precomputedScore = getHashScoreMap().get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // Find move with the best score
    final List<Game.Move> moves = findPossibleMoves(game);
    Integer bestScore = null;
    for (final Game.Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        newGame.setCellValue(move.rowIdx, move.colIdx, move.player);
        final int curScore = minimax(newGame, player, level + 1);
        if (player == move.player) {
          if (bestScore == null || curScore > bestScore) {
            bestScore = curScore;
          }
        }
        else {
          if (bestScore == null || curScore < bestScore) {
            bestScore = curScore;
          }
        }
      }
      catch (DimensionException | StateException e) {
        Logger.error(e, "could not copy game state");
      }
    }

    // Update hashScoreMap
    synchronized (getHashScoreMap()) {
      final long hash = game.getBoardHash();
      getHashScoreMap().put(hash, bestScore);
    }

    // Return best score
    return bestScore;
  }

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
    Integer bestScore = null;
    final List<Game.Move> bestMoves = new ArrayList<>();
    for (final Game.Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
        final int curScore = minimax(newGame, curPlayer, 0);
        move.setScore(curScore);
        if (bestScore == null || curScore > bestScore) {
          bestScore = curScore;
          bestMoves.clear();
          bestMoves.add(move);
        }
        else if (bestScore != null && curScore == bestScore) {
          bestMoves.add(move);
        }
      }
      catch (DimensionException | StateException e) {
        Logger.error(e, "could not copy game state");
      }
    }

    // Return result
    return selectMove(bestMoves);
  }
}
