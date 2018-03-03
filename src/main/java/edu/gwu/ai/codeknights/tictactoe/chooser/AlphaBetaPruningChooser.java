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
  public static final boolean DEFAULT_GRAPH_SEARCH = true;

  private final AbstractCellFilter filter;
  private boolean randomShuffle;
  private boolean graphSearch;

  private final Map<String, Long> scoreMap;

  public AlphaBetaPruningChooser(final AbstractCellFilter filter, final boolean randomShuffle,
    final boolean graphSearch) {
    this.filter = filter != null ? filter : DEFAULT_FILTER;
    this.randomShuffle = randomShuffle;
    this.graphSearch = graphSearch;

    scoreMap = new HashMap<>();
  }

  public AlphaBetaPruningChooser(final AbstractCellFilter filter) {
    this(filter, DEFAULT_RANDOM_SHUFFLE, DEFAULT_GRAPH_SEARCH);
  }

  public AlphaBetaPruningChooser(final boolean randomShuffle, final boolean graphSearch) {
    this(DEFAULT_FILTER, randomShuffle, graphSearch);
  }

  public AlphaBetaPruningChooser() {
    this(DEFAULT_FILTER, DEFAULT_RANDOM_SHUFFLE, DEFAULT_GRAPH_SEARCH);
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

  public boolean isGraphSearch() {
    return graphSearch;
  }

  public void setGraphSearch(final boolean graphSearch) {
    this.graphSearch = graphSearch;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    if (randomShuffle) {
      Collections.shuffle(cells);
    }
    final Player player = game.getNextPlayer();
    final Player opponent = game.getOtherPlayer(player);
    final Game copy = game.getCopy();
    Long maxScore = null;
    final Set<Cell> bestCells = new HashSet<>();
    final int maxDepth = copy.getBoard().countEmpty();
    int curMaxLevel = 1;
    while (curMaxLevel < maxDepth) {
      for (final Cell cell : cells) {
        final Cell copyCell = copy.getBoard().getCell(cell.getRowIdx(), cell.getColIdx());
        if (copyCell.isEmpty()) {
          copyCell.setPlayer(player);
          final long score = abp(copy, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 0, curMaxLevel);
          copyCell.setPlayer(null);
          if (maxScore == null || score >= maxScore) {
            if (maxScore != null && score > maxScore) {
              bestCells.clear();
            }
            maxScore = score;
            bestCells.add(cell);
          }
        }
      }
      curMaxLevel++;
    }
    return bestCells.stream().findAny().orElse(null);
  }

  public long abp(final Game game, final Player player, final Player opponent, long alpha, long beta,
    final int curLevel, final int maxLevel) {

    // Check for terminal state
    if (curLevel >= maxLevel || game.isGameOver()) {
      //TODO: will our scoreMap contain hastily computed values that cause problems at deeper levels?
      //TODO: try without score map?
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
    if (graphSearch) {
      final String hash = game.getBoardHash();
      synchronized (scoreMap) {
        final Long precomputedScore = scoreMap.get(hash);
        if (precomputedScore != null) {
          return precomputedScore;
        }
      }
    }

    // TODO: need to make a copy of the game here ? for parallelism ? don't modify what's been passed in from above...

    // Try all possible moves
    final List<Cell> filteredCells = filter.filterCells(game).collect(Collectors.toList());
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
    final long bestScore = player.equals(curPlayer) ? alpha : beta;

    // Update score map
    if (graphSearch) {
      final String hash = game.getBoardHash();
      synchronized (scoreMap) {
        scoreMap.put(hash, bestScore);
      }
    }

    // Return best score
    return bestScore;
  }
}
