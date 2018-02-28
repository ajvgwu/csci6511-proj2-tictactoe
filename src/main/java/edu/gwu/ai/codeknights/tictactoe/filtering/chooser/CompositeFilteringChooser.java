package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;
import edu.gwu.ai.codeknights.tictactoe.filtering.filter.AbstractCellFilter;

public class CompositeFilteringChooser extends AbstractCellChooser {

  public static final AbstractCellChooser DEFAULT_CHOOSER = new AnyCellChooser();

  private final List<AbstractCellFilter> filters;
  private final AbstractCellChooser chooser;

  public CompositeFilteringChooser(final List<AbstractCellFilter> filters, final AbstractCellChooser chooser) {
    this.filters = filters != null ? filters : Collections.emptyList();
    this.chooser = chooser != null ? chooser : DEFAULT_CHOOSER;
  }

  public List<AbstractCellFilter> getFilters() {
    return filters;
  }

  public AbstractCellChooser getChooser() {
    return chooser;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    Stream<Cell> reducedInput = input;
    for (final AbstractCellFilter filter : filters) {
      reducedInput = filter.filterCells(reducedInput, game);
    }
    return chooser.chooseCell(reducedInput, game);
  }
}
