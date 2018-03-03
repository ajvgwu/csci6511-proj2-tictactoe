package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;

public class AbpTimeLimitChooser extends AlphaBetaPruningChooser {

  private final int limitSec;

  public AbpTimeLimitChooser(final int limitSec, final AbstractCellFilter filter) {
    super(filter);

    this.limitSec = limitSec;
  }

  public AbpTimeLimitChooser(final int limitSec) {
    this(limitSec, null);
  }

  public int getLimitSec() {
    return limitSec;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    if (isRandomShuffle()) {
      Collections.shuffle(cells);
    }
    final CellChooser chooser = new CellChooser(cells, game);
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<Cell> task = executor.submit(chooser);
    try {
      return task.get(limitSec, TimeUnit.SECONDS);
    }
    catch (TimeoutException | ExecutionException | InterruptedException e) {
      Logger.error(e, "did not complete in time limit: " + String.valueOf(limitSec) + " seconds");
      task.cancel(true);
    }
    finally {
      executor.shutdownNow();
    }
    final Set<Cell> bestCells = chooser.getBestCells();
    if (bestCells != null) {
      return bestCells.stream().findAny().orElse(null);
    }
    else {
      Logger.error("did not find any candidate cells within time limit: {} seconds", limitSec);
      return null;
    }
  }

  protected class CellChooser implements Callable<Cell> {

    private final List<Cell> cells;
    private final Game game;

    private Long maxScore;
    private Set<Cell> bestCells;

    public CellChooser(final List<Cell> cells, final Game game) {
      this.cells = cells;
      this.game = game;

      maxScore = null;
      bestCells = null;
    }

    public Long getMaxScore() {
      return maxScore;
    }

    public Set<Cell> getBestCells() {
      return bestCells;
    }

    @Override
    public Cell call() {
      final Player player = game.getNextPlayer();
      final Player opponent = game.getOtherPlayer(player);
      final Game copy = game.getCopy();
      maxScore = null;
      bestCells = new HashSet<>();
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

        // Increase max level and clear score map
        curMaxLevel++;
        // getScoreMap().clear(); // TODO: clear map ???
      }
      return bestCells.stream().findAny().orElse(null);
    }
  }
}
