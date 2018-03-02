package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.filter.AbstractCellFilter;

public class CompositeFilteringChooser extends AbstractCellChooser {

  public static final AbstractCellChooser DEFAULT_CHOOSER = new MaxUtilityChooser();

  private final List<AbstractCellFilter> filters;
  private final AbstractCellChooser chooser;

  public CompositeFilteringChooser(final List<AbstractCellFilter> filters, final AbstractCellChooser chooser) {
    this.filters = new ArrayList<>();
    this.filters.addAll(filters);
    this.chooser = chooser != null ? chooser : DEFAULT_CHOOSER;
  }

  public List<AbstractCellFilter> getFilters() {
    return filters;
  }

  public AbstractCellChooser getChooser() {
    return chooser;
  }

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    List<Cell> reducedCells = input.collect(Collectors.toList());
    for (final AbstractCellFilter filter : filters) {
      final List<Cell> newCells = filter.filterCells(reducedCells.stream(), game).collect(Collectors.toList());
      if (newCells.size() > 0) {
        reducedCells = newCells;
      }
    }
    return chooser.chooseCell(reducedCells.stream(), game);
  }
}
