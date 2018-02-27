package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Move;
import edu.gwu.ai.codeknights.tictactoe.core.exception.DimensionException;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.exception.StateException;
import org.pmw.tinylog.Logger;

import java.util.*;

public class AlphaBetaPruningChooser extends AIMoveChooser {


  public AlphaBetaPruningChooser(){
  }

  protected long alphabetapruning(final Game game, final int player,
                                  long alpha, long beta, int level, int curMax) {
    Long bestScore = null;
    Long bestMove = null;
    final int curPlayer = game.getNextPlayer();
    long upperBound = game.getDim() * game.getDim();
    // Check for terminal state
    if (level++ >= curMax || game.isGameOver()) {
      if (game.didPlayerWin(player)) {
        bestScore = upperBound - level;
      } else if (game.didAnyPlayerWin()) {
        bestScore = -upperBound + level;
      } else {
        bestScore = 0L;
      }
      return bestScore;
    }

    // Check if we've already solved this state
    synchronized (getHashScoreMap()) {
      final long hash = game.getBoardHash();
      final Long precomputedScore = getHashScoreMap().get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // Find move with the best score
    List<Move> moves = yetAnotherMoveFinder( game);

    try {
      final Game newGame = game.getCopy();

      for (final Move move : moves) {
        newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
        final long curScore = alphabetapruning(newGame, player, alpha, beta,
            level, curMax);
        if (player == curPlayer) {
          if (curScore > alpha) {
            alpha = curScore;
          }
        } else {
          if (curScore < beta) {
            beta = curScore;
          }
        }
        if (alpha >= beta) {
          break;
        }

        // reset the move
        newGame.setCellValue(move.rowIdx, move.colIdx, null);
      }
    } catch (DimensionException | StateException e) {
      Logger.error(e, "could not copy game state");
    }

    // if the play is next player
    // choose alpha
    if (player == curPlayer) {
      bestScore = alpha;
    } else {
      bestScore = beta;
    }

    // Update hashScoreMap
    synchronized (getHashScoreMap()) {
      final long hash = game.getBoardHash();
      getHashScoreMap().put(hash, bestScore);
    }

    // Return best score
    return bestScore;
  }

//  @Override
//  public abstract Move findNextMove(final Game game);

  @Override
  public Move findNextMove(final Game game) {
    if (game.isGameOver()) {
      return null;
    }
    final int numFirst = game.countFirstPlayer();
    final int numOther = game.countOtherPlayer();
    final int curPlayer = game.getNextPlayer();
    if (numFirst + numOther == 0) {
      final int dim = game.getDim();
      final int center = dim / 2;
      return new Move(center, center, curPlayer, null);
    }
    final List<Move> moves = yetAnotherMoveFinder(game);
    Long bestScore = null;
    final Set<Move> bestMoves = new HashSet<>();

    try {
      final Game newGame = game.getCopy();
      int maxDepth = newGame.countEmpty();
      int curMaxLevel = 0;
      while (curMaxLevel < maxDepth){
        curMaxLevel++;
        for (final Move move : moves) {
          newGame.setCellValue(move.rowIdx, move.colIdx, curPlayer);
          Long curScore = alphabetapruning(newGame, curPlayer,
              Long.MIN_VALUE, Long.MAX_VALUE, 0, curMaxLevel);
          move.setScore(curScore);
          if (bestScore == null || curScore > bestScore) {
            bestScore = curScore;
            bestMoves.clear();
            bestMoves.add(move);
          }
          else if (curScore.equals(bestScore)) {
            bestMoves.add(move);
          }
          newGame.setCellValue(move.rowIdx, move.colIdx, null);
        }

/*        Move move = bestMoves.stream().max(Comparator.comparingLong
            (Move::getScore)).orElse(null);
        if(move != null && move.getScore() > 0L){
          break;
        }*/
      }
    }
    catch (DimensionException | StateException e) {
      Logger.error(e, "could not copy game state");
    }

    // Return result
    return selectMove(game, new ArrayList<>(bestMoves));
  }
}
