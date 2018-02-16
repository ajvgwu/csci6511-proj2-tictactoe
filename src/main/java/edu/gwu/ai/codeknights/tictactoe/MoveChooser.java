package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.pmw.tinylog.Logger;

public class MoveChooser {

  private final Map<Long, Integer> hashScoreMap;

  public MoveChooser() {
    hashScoreMap = new HashMap<>();
  }

  protected List<Move> findPossibleMoves(final Game game) {
    final List<Move> moves = new ArrayList<>();
    final int dim = game.getDim();
    final int curPlayer = game.getNextPlayer();
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Integer value = game.getCellValue(rowIdx, colIdx);
        if (value == null) {
          moves.add(new Move(rowIdx, colIdx, curPlayer, null));
        }
      }
    }
    return moves;
  }

  protected int minimax(final Game game, final int player, final int level) {
    // Check for terminal state
    if (level == game.getDim() * 2 || game.isGameOver()) {
      int score = 0;
      if (game.didPlayerWin(player)) {
        score = 10;
      }
      else if (game.didAnyPlayerWin()) {
        score = -10;
      }
      return score;
    }

    // Check if we've already solved this state
    synchronized (hashScoreMap) {
      final long hash = game.getBoardHash();
      final Integer precomputedScore = hashScoreMap.get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // Find move with the best score
    final List<Move> moves = findPossibleMoves(game);
    Integer bestScore = null;
    for (final Move move : moves) {
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
    synchronized (hashScoreMap) {
      final long hash = game.getBoardHash();
      hashScoreMap.put(hash, bestScore);
    }

    // Return best score
    return bestScore;
  }

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
    Integer bestScore = null;
    final List<Move> bestMoves = new ArrayList<>();
    for (final Move move : moves) {
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

    if (bestMoves.size() > 1) {
      // If many moves scored equally, choose randomly from among them
      return bestMoves.get(new Random().nextInt(bestMoves.size()));
    }
    else if (bestMoves.size() > 0) {
      // Found a single best move
      return bestMoves.get(0);
    }
    else {
      // No move found !!!
      return null;
    }
  }

  public static class Move {

    public final int rowIdx;
    public final int colIdx;
    public final int player;

    private Integer score;

    public Move(final int rowIdx, final int colIdx, final int player, final Integer score) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.player = player;

      this.score = score;
    }

    public Integer getScore() {
      return score;
    }

    public void setScore(final Integer score) {
      this.score = score;
    }

    @Override
    public String toString() {
      return String.format("Player %d at (%d,%d) scores %d", player, rowIdx, colIdx, score);
    }
  }
}
