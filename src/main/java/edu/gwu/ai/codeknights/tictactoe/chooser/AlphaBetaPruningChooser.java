package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

public class AlphaBetaPruningChooser extends AbstractCellChooser {

  public static final AbstractCellFilter DEFAULT_FILTER = new PopulatedNeighborFilter();
  public static final boolean DEFAULT_RANDOM_SHUFFLE = true;

  private final AbstractCellFilter filter;
  private boolean randomShuffle;

  private Map<String, Long> scoreMap;
  private Long bestScore;
  private Set<Cell> bestCells;

  public AlphaBetaPruningChooser(final AbstractCellFilter filter, final boolean randomShuffle) {
    this.filter = filter != null ? filter : DEFAULT_FILTER;
    this.randomShuffle = randomShuffle;

    scoreMap = null;
    bestScore = null;
    bestCells = null;
  }

  public AlphaBetaPruningChooser(final AbstractCellFilter filter) {
    this(filter, DEFAULT_RANDOM_SHUFFLE);
  }

  public AlphaBetaPruningChooser(final boolean randomShuffle) {
    this(DEFAULT_FILTER, randomShuffle);
  }

  public AlphaBetaPruningChooser() {
    this(DEFAULT_FILTER, DEFAULT_RANDOM_SHUFFLE);
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

  protected Map<String, Long> getScoreMap() {
    return scoreMap;
  }

  protected Long getBestScore() {
    return bestScore;
  }

  protected Set<Cell> getBestCells() {
    return bestCells;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    scoreMap = new HashMap<>();
    bestScore = null;
    bestCells = new HashSet<>();
    final List<Cell> cells = input.collect(Collectors.toList());
    if (isRandomShuffle()) {
      Collections.shuffle(cells);
    }
    final Player player = game.getNextPlayer();
    final Player opponent = game.getOtherPlayer(player);
    final Game copy = game.getCopy();
    final int maxDepth = copy.getBoard().countEmpty();
    int curMaxLevel = 1;
    while (curMaxLevel < maxDepth) {
      for (final Cell cell : cells) {
        final Cell copyCell = copy.getBoard().getCell(cell.getRowIdx(), cell.getColIdx());
        if (copyCell.isEmpty()) {
          copyCell.setPlayer(player);
          final long score = abp(copy, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 0, curMaxLevel);
          copyCell.setPlayer(null);
          if (bestScore == null || score >= bestScore) {
            if (bestScore != null && score > bestScore) {
              bestCells.clear();
            }
            bestScore = score;
            bestCells.add(cell);
          }
        }
      }

      // Increase max level and clear score map
      curMaxLevel++;
      // getScoreMap().clear(); // TODO: clear map ???
    }
    return bestCells.stream().findAny().orElse(null);
  }

  public long abp(final Game game, final Player player, final Player opponent, long alpha, long beta,
    final int curLevel, final int maxLevel) {

    // Check for terminal state
    if (curLevel > maxLevel || game.isGameOver()) {
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
    synchronized (getScoreMap()) {
      final String hash = game.getBoardHash();
      final Long precomputedScore = getScoreMap().get(hash);
      if (precomputedScore != null) {
        return precomputedScore;
      }
    }

    // Try all possible moves
    final List<Cell> filteredCells = getFilter().filterCells(game).collect(Collectors.toList());
    final Player curPlayer = game.getNextPlayer();
    for (final Cell cell : filteredCells) {
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
    final long score = player.equals(curPlayer) ? alpha : beta;

    // Update score map
    synchronized (getScoreMap()) {
      final String hash = game.getBoardHash();
      getScoreMap().put(hash, score);
    }

    // Return best score
    return score;
  }
}
