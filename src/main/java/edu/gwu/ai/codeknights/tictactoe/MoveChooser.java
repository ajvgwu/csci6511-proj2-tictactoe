package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pmw.tinylog.Logger;

public class MoveChooser {

  public static final int SCORE_WIN = 10;
  public static final int SCORE_LOSS = -10;
  public static final int SCORE_DRAW = 0;

  protected List<Move> findPossibleMoves(final Game game) {
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = numFirst > numOther ? Game.OTHER_PLAYER_VALUE : Game.FIRST_PLAYER_VALUE;
    final List<Move> moves = new ArrayList<>();
    final int dim = game.getDim();
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Integer value = game.getCellValue(rowIdx, colIdx);
        if (value == null) {
          moves.add(new Move(rowIdx, colIdx, curPlayer));
        }
      }
    }
    return moves;
  }

  protected int minimax(final Game game, final int player) {
    // Check for terminal state
    if (game.isGameOver()) {
      if (game.didPlayerWin(player)) {
        return SCORE_WIN;
      }
      else if (game.didAnyPlayerWin()) {
        return SCORE_LOSS;
      }
      else {
        return SCORE_DRAW;
      }
    }

    // Find move with the best score
    final List<Move> moves = findPossibleMoves(game);
    Integer bestScore = null;
    for (final Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        newGame.setCellValue(move.rowIdx, move.colIdx, move.player);
        final int curScore = minimax(newGame, player);
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
    return bestScore;
  }

  protected Move findBestMove(final Game game) {
    if (game.isGameOver()) {
      return null;
    }
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = numFirst > numOther ? Game.OTHER_PLAYER_VALUE : Game.FIRST_PLAYER_VALUE;
    if (numFirst + numOther == 0) {
      final int dim = game.getDim();
      final int center = (int) (dim / 2);
      return new Move(center, center, curPlayer);
    }
    final List<Move> moves = findPossibleMoves(game);
    Integer bestScore = null;
    final List<Move> bestMoves = new ArrayList<>();
    for (final Move move : moves) {
      try {
        final Game newGame = game.getCopy();
        newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
        final int curScore = minimax(newGame, curPlayer);
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

  public void makeBestMove(final Game game) {
    final Move move = findBestMove(game);
    game.setCellValue(move.rowIdx, move.colIdx, move.player);
  }

  public static class Move {

    public final int rowIdx;
    public final int colIdx;
    public final int player;

    public Move(final int rowIdx, final int colIdx, final int player) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.player = player;
    }
  }
}
