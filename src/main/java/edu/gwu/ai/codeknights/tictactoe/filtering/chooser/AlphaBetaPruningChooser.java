package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class AlphaBetaPruningChooser extends AbstractCellChooser {

  private static final Map<String, Long> SCORE_MAP = new HashMap<>();

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final Player player = game.getNextPlayer();
    final Player opponent = game.getOtherPlayer(player);
    input.parallel()
      .max(new Comparator<Cell>() {

        @Override
        public int compare(final Cell a, final Cell b) {
          final long scoreA = abp(game, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 0, game.getDim() * 2);
          final long scoreB = abp(game, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 0, game.getDim() * 2);
          final long diff = scoreA - scoreB;
          // TODO: finish --- this may not be right !!!
          return diff < 0 ? -1 : diff == 0 ? 0 : 1;
        }
      });

    // TODO: finish
    return null;
  }

  public static long abp(final TicTacToeGame game, final Player player, final Player opponent, long alpha, long beta,
    final int curLevel, final int maxLevel) {

    // Check for terminal state
    if (curLevel >= maxLevel || game.isGameOver()) {
      final long utility = game.evaluatePlayerUtility(player);
      if (game.didPlayerWin(player)) {
        return Math.max(1L, utility);
      }
      else if (game.didPlayerWin(opponent)) {
        return Math.min(-1L, utility);
      }
      else {
        return utility;
      }
    }

    // Check if we've already solved this state
    final String hash = game.getBoardHash();
    synchronized (SCORE_MAP) {
      final Long precomputedScore = SCORE_MAP.get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // TODO: need to make a copy of the game here ? for parallelism ? don't modify what's been passed in from above...

    // Try all possible moves
    final Player curPlayer = game.getNextPlayer();
    for (final Cell cell : game.getBoard().getEmptyCells()) {
      cell.setPlayer(curPlayer);
      final long curScore = abp(game, player, opponent, alpha, beta, curLevel + 1, maxLevel);
      cell.setPlayer(null);
      if (player.equals(curPlayer)) {
        if (curScore > alpha) {
          alpha = curScore;
        }
      }
      else {
        if (curScore < beta) {
          beta = curScore;
        }
      }
      if (alpha >= beta) {
        break;
      }
    }
    final long bestScore = player.equals(curPlayer) ? alpha : beta;

    // Update hashScoreMap
    synchronized (SCORE_MAP) {
      SCORE_MAP.put(hash, bestScore);
    }

    // Return best score
    return bestScore;
  }
}
