package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;
import org.pmw.tinylog.Logger;

import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Stream;

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

  protected Cell callForTask(final Stream<Cell> input, final Game game) {
    return super.chooseCell(input, game);
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<Cell> task = executor.submit(new Callable<Cell>() {

      @Override
      public Cell call() {
        return callForTask(input, game);
      }
    });
    try {
      return task.get(limitSec, TimeUnit.SECONDS);
    }
    catch (TimeoutException | ExecutionException | InterruptedException e) {
      Logger.warn("did not complete in time limit: {} seconds", limitSec);
      task.cancel(true);
    }
    finally {
      executor.shutdownNow();
    }
    final Set<Cell> bestCells = getBestCells();
    if (bestCells != null) {
      return bestCells.stream().findAny().orElse(null);
    }
    else {
      Logger.error("did not find any candidate cells within time limit: {} seconds", limitSec);
      return null;
    }
  }
}
