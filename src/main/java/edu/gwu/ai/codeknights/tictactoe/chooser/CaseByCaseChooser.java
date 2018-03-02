package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

public class CaseByCaseChooser extends AbstractCellChooser {

  private final PopulatedNeighborFilter neighborFilter;

  private final PairingChooser pairingChooser;
  private final RuleBasedChooser ruleBasedChooser;
  private final AlphaBetaPruningChooser abpChooser;

  public CaseByCaseChooser() {
    neighborFilter = new PopulatedNeighborFilter();

    pairingChooser = new PairingChooser();
    ruleBasedChooser = new RuleBasedChooser();
    abpChooser = new AlphaBetaPruningChooser(neighborFilter);
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    final List<Cell> cells = input.collect(Collectors.toList());

    // Try pairing strategy
    Cell choice = pairingChooser.chooseCell(cells.stream(), game);
    if (choice != null) {
      return choice;
    }

    // Try rule-based strategy
    choice = ruleBasedChooser.chooseCell(cells.stream(), game);
    if (choice != null) {
      return choice;
    }

    // Otherwise, play smartly
    abpChooser.setMaxDepth(Math.min(8, game.getDim() * 2));
    choice = abpChooser.chooseCell(neighborFilter.filterCells(cells.stream(), game), game);
    return choice;
  }
}
