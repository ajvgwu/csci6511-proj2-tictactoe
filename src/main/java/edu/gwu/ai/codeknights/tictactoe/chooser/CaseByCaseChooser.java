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

  public CaseByCaseChooser(final AlphaBetaPruningChooser abp) {
    pairingChooser = new PairingChooser();
    ruleBasedChooser = new RuleBasedChooser();
    abpChooser = abp;
  }

  public CaseByCaseChooser(final AbstractCellFilter abpFilter) {
    this(new AlphaBetaPruningChooser(abpFilter));
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
    choice = abpChooser.chooseCell(cells.stream(), game);
    return choice;
  }
}
