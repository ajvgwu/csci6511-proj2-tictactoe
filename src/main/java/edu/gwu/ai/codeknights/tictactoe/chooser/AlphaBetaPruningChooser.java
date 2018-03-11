package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlphaBetaPruningChooser extends AbstractCellChooser {

  private AbstractCellFilter filter;

  private final Map<String, SearchResult> resultMap;

  private Long bestScore;
  private Set<Cell> bestCells;

  public AlphaBetaPruningChooser(final AbstractCellFilter filter) {
    this.filter = filter;

    resultMap = new HashMap<>();

    bestScore = null;
    bestCells = null;
  }

  public AlphaBetaPruningChooser() {
    this(null);
  }

  public AbstractCellFilter getFilter() {
    return filter;
  }

  public void setFilter(final AbstractCellFilter filter) {
    this.filter = filter;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    // Starting a new search, initialize results
    bestScore = null;
    bestCells = new HashSet<>();

    // Shuffle input cells
    final List<Cell> cells = new ArrayList<>(input.collect(Collectors.toList()));
    Collections.shuffle(cells);

    // Create a copy of the game
    final Game copy = game.getCopy();
    final Player player = copy.getNextPlayer();
    final Player opponent = copy.getOtherPlayer(player);

    // Set max depth to number of empty spaces
    int curMaxDepth = 1;
    final int maxDepth = copy.getBoard().countEmpty();
    while (curMaxDepth < maxDepth) {

      // Player tries each available cell
      for (final Cell cell : cells) {
        final Cell copyCell = copy.getBoard().getCell(cell.getRowIdx(), cell.getColIdx());
        if (copyCell.isEmpty()) {
          copyCell.setPlayer(player);
          final SearchResult result = abp(copy, player, opponent, Long.MIN_VALUE, Long.MAX_VALUE, 1, curMaxDepth);
          copyCell.setPlayer(null);
          if (bestScore == null || result.getScore() >= bestScore) {
            if (bestScore != null && result.getScore() > bestScore) {
              bestCells.clear();
            }
            bestScore = result.getScore();
            bestCells.add(cell);
          }
        }
      }

      // Increase max depth (iterative deepening)
      curMaxDepth++;
    }

    // Return any of the equally-best cells
    return bestCells.stream().findAny().orElse(null);
  }

  public SearchResult abp(final Game game, final Player player, final Player opponent, long alpha, long beta,
    final int curDepth, final int maxDepth) {

    // Check for terminal state or stopping condition
    final boolean isGameOver = game.isGameOver();
    if (isGameOver || curDepth >= maxDepth) {
      long utility = game.evaluatePlayerUtility(player);
      if (isGameOver) {
        if (game.didPlayerWin(player)) {
          utility = Math.max(1L, utility);
        }
        else if (game.didPlayerWin(opponent)) {
          utility = Math.min(-1L, utility);
        }
      }
      final SearchResult result = new SearchResult(curDepth, maxDepth, isGameOver, utility);
      return result;
    }

    // Check if we've already solved this state
    synchronized (resultMap) {
      final String hash = game.getBoardHash();
      final SearchResult result = resultMap.get(hash);
      if (result != null) {
        if (result.didTerminate() || result.getDepthReached() >= curDepth) {
          return result;
        }
      }
    }

    // Filter and shuffle cells
    final List<Cell> filteredCells = new ArrayList<>();
    if (filter != null) {
      filteredCells.addAll(filter.filterCells(game).collect(Collectors.toList()));
    }
    else {
      filteredCells.addAll(game.getBoard().getEmptyCells());
    }
    Collections.shuffle(filteredCells);

    // Try all possible moves
    final Player curPlayer = game.getNextPlayer();
    SearchResult bestResult = null;
    for (final Cell cell : filteredCells) {
      if (cell.isEmpty()) {
        cell.setPlayer(curPlayer);
        final SearchResult result = abp(game, player, opponent, alpha, beta, curDepth + 1, maxDepth);
        cell.setPlayer(null);
        if (player.equals(curPlayer)) {
          if (result.getScore() > alpha) {
            alpha = result.getScore();
            bestResult = result;
          }
        }
        else {
          if (result.getScore() < beta) {
            beta = result.getScore();
            bestResult = result;
          }
        }
        if (alpha >= beta) {
          break;
        }
      }
    }

    // Update score map
    synchronized (resultMap) {
      final String hash = game.getBoardHash();
      resultMap.put(hash, bestResult);
    }

    // Return best result
    return bestResult;
  }

  protected Set<Cell> getBestCells() {
    return bestCells;
  }

  public static class SearchResult {

    private int depthReached;
    private int maxDepth;
    private boolean didTerminate;
    private long score;

    public SearchResult(final int depthReached, final int maxDepth, final boolean didTerminate, final long score) {
      this.depthReached = depthReached;
      this.maxDepth = maxDepth;
      this.didTerminate = didTerminate;
      this.score = score;
    }

    public int getDepthReached() {
      return depthReached;
    }

    public void setDepthReached(final int depthReached) {
      this.depthReached = depthReached;
    }

    public int getMaxDepth() {
      return maxDepth;
    }

    public void setMaxDepth(final int maxDepth) {
      this.maxDepth = maxDepth;
    }

    public boolean didTerminate() {
      return didTerminate;
    }

    public void setDidTerminate(final boolean didTerminate) {
      this.didTerminate = didTerminate;
    }

    public long getScore() {
      return score;
    }

    public void setScore(final long score) {
      this.score = score;
    }

    @Override
    public String toString() {
      return new StringBuilder()
        .append("depthReached=").append(depthReached)
        .append(", maxDepth=").append(maxDepth)
        .append(", didTerminate=").append(didTerminate)
        .append(", score=").append(score)
        .toString();
    }
  }
}
