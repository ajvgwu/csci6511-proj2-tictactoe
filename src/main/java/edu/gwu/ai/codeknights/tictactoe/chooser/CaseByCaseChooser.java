package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;

public class CaseByCaseChooser extends AbstractCellChooser {

  private final PairingChooser pairingChooser;
  private final RuleBasedChooser ruleBasedChooser;
  private final AlphaBetaPruningChooser abpChooser;

  public CaseByCaseChooser(final AbstractCellFilter abpFilter, final Boolean abpGraphSearch) {
    pairingChooser = new PairingChooser();
    ruleBasedChooser = new RuleBasedChooser();
    abpChooser = new AlphaBetaPruningChooser(abpFilter);
    if (abpGraphSearch != null) {
      abpChooser.setGraphSearch(abpGraphSearch);
    }
  }

  public CaseByCaseChooser(final AbstractCellFilter abpFilter) {
    this(abpFilter, null);
  }

  public CaseByCaseChooser(final boolean abpGraphSearch) {
    this(null, abpGraphSearch);
  }

  public CaseByCaseChooser() {
    this(null, null);
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
    choice = abpChooser.chooseCell(cells.stream(), game);
    return choice;
  }
}
