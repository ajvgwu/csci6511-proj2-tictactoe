package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.filtering.filter.AbstractCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filtering.filter.BestOpenSublineFilter;

public class AlphaBetaPruningChooser extends AbstractCellChooser {

  public static final AbstractCellFilter DEFAULT_FILTER = new BestOpenSublineFilter();
  public static final boolean DEFAULT_RANDOM_SHUFFLE = true;

  private final AbstractCellFilter filter;
  private boolean randomShuffle;
  private Integer maxDepth;

  private final Map<String, Long> scoreMap;

  public AlphaBetaPruningChooser(final AbstractCellFilter filter, final boolean randomShuffle, final Integer maxDepth) {
    this.filter = filter != null ? filter : DEFAULT_FILTER;
    this.randomShuffle = randomShuffle;
    this.maxDepth = maxDepth;

    scoreMap = new HashMap<>();
  }

  public AlphaBetaPruningChooser(final AbstractCellFilter filter) {
    this(filter, DEFAULT_RANDOM_SHUFFLE, null);
  }

  public AlphaBetaPruningChooser(final Integer maxDepth) {
    this(DEFAULT_FILTER, DEFAULT_RANDOM_SHUFFLE, maxDepth);
  }

  public AlphaBetaPruningChooser() {
    this(DEFAULT_FILTER, DEFAULT_RANDOM_SHUFFLE, null);
  }

  public AbstractCellFilter getFilter() {
    return filter;
  }

  public boolean isRandomShuffle() {
    return randomShuffle;
  }

  public void setRandomShuffle(final boolean randomShuffle) {
    this.randomShuffle = randomShuffle;
  }

  public Integer getMaxDepth() {
    return maxDepth;
  }

  public void setMaxDepth(final Integer maxDepth) {
    this.maxDepth = maxDepth;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    if (randomShuffle) {
      Collections.shuffle(cells);
    }
    final Player player = game.getNextPlayer();
    final Player opponent = game.getOtherPlayer(player);
    final TicTacToeGame copy = game.getCopy();
    Long maxScore = null;
    Cell bestCell = null;
    for (final Cell cell : cells) {
      final Cell copyCell = copy.getBoard().getCell(cell.getRowIdx(), cell.getColIdx());
      if (copyCell.isEmpty()) {
        copyCell.setPlayer(player);
        final long score = abp(copy, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 0);
        copyCell.setPlayer(null);
        if (maxScore == null || score > maxScore) {
          maxScore = score;
          bestCell = cell;
        }
      }
    }
    return bestCell;
  }

  public long abp(final TicTacToeGame game, final Player player, final Player opponent, long alpha, long beta,
    final int curLevel) {

    // Check for terminal state
    if (maxDepth != null && curLevel >= maxDepth || game.isGameOver()) {
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
    synchronized (scoreMap) {
      final Long precomputedScore = scoreMap.get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // TODO: need to make a copy of the game here ? for parallelism ? don't modify what's been passed in from above...

    // Try all possible moves
    final List<Cell> filteredCells = filter.filterCells(game).collect(Collectors.toList());
    final Player curPlayer = game.getNextPlayer();
    for (final Cell cell : filteredCells) {
      cell.setPlayer(curPlayer);
      final long curScore = abp(game, player, opponent, alpha, beta, curLevel + 1);
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
    synchronized (scoreMap) {
      scoreMap.put(hash, bestScore);
    }

    // Return best score
    return bestScore;
  }
}
