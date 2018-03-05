package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.EmptyCellFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

public class CaseByCaseChooser extends AbstractCellChooser {

  private final RuleBasedChooser ruleBasedChooser;
  private final AlphaBetaPruningChooser abpChooser;

  private final PopulatedNeighborFilter abpNeighborFilter;
  private final EmptyCellFilter abpAllEmptyFilter;

  public CaseByCaseChooser(final AlphaBetaPruningChooser abp) {
    ruleBasedChooser = new RuleBasedChooser();
    abpChooser = abp;

    abpNeighborFilter = new PopulatedNeighborFilter();
    abpAllEmptyFilter = new EmptyCellFilter();
  }

  public CaseByCaseChooser() {
    this(new AlphaBetaPruningChooser());
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final List<Cell> cells = input.collect(Collectors.toList());

    // Try rule-based strategy
    Cell choice = ruleBasedChooser.chooseCell(cells.stream(), game);
    if (choice != null) {
      return choice;
    }

    // Otherwise, play smartly
    final int dim = game.getDim();
    final int numCells = dim * dim;
    final int numEmpty = game.getBoard().countEmpty();
    final int numPopulated = numCells - numEmpty;
    AbstractCellFilter filter = abpAllEmptyFilter;
    if (numPopulated < 3) {
      filter = abpNeighborFilter;
    }
    abpChooser.setFilter(filter);
    choice = abpChooser.chooseCell(filter.filterCells(cells.stream(), game), game);
    return choice;
  }
}
