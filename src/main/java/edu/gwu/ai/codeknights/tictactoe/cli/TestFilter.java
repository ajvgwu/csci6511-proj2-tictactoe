package edu.gwu.ai.codeknights.tictactoe.cli;

import java.util.List;
import java.util.stream.Collectors;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filter.Filter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
  name = "test-filter", sortOptions = false, showDefaultValues = true,
  description = "test the given filter on the current board state")
public class TestFilter extends AbstractOfflineSubcommand {

  @Option(
    names = {"--filter"},
    description = "filter to test on the current board state")
  private Filter filter = Filter.POPULATED_NEIGHBOR;

  @Override
  protected void validateArgs() throws Exception {
    super.validateArgs();
    if (filter == null) {
      filter = Filter.POPULATED_NEIGHBOR;
      throw new IllegalArgumentException("must select filter");
    }
  }

  protected Filter getFilter() {
    return filter;
  }

  @Override
  public Void call() throws Exception {
    validateArgs();

    // Create game
    final Game game = createGame();

    // Test the filter
    final GameplayHelper helper = new GameplayHelper(game);
    System.out.println("### Initial state ###");
    helper.printCurGameInfo();
    final List<Cell> candidates = getFilter().createFilter().filterCells(game).collect(Collectors.toList());
    final Player blankMarker = new Player(0, ' ');
    final Player candidateMarker = new Player(0, '?');
    for (final Cell cell : game.getBoard().getAllCells()) {
      if (cell.isEmpty()) {
        cell.setPlayer(blankMarker);
      }
    }
    for (final Cell cell : candidates) {
      cell.setPlayer(candidateMarker);
    }
    System.out.println();
    System.out.println("### Showing candidates as '" + String.valueOf(candidateMarker.getMarker()) + "' ###");
    helper.printCurGameInfo();

    // Done.
    return null;
  }
}
