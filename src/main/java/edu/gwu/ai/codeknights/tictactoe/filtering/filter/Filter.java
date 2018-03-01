package edu.gwu.ai.codeknights.tictactoe.filtering.filter;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Filter {

  EMPTY_CELL("EmptyCell") {

    @Override
    public AbstractCellFilter createFilter() {
      return new EmptyCellFilter();
    }
  },
  BEST_OPEN_SUBLINE("BestOpenSubline") {

    @Override
    public AbstractCellFilter createFilter() {
      return new BestOpenSublineFilter();
    }
  },
  POPULATED_NEIGHBOR("PopulatedNeighbor") {

    @Override
    public AbstractCellFilter createFilter() {
      return new PopulatedNeighborFilter();
    }
  };

  private final String name;

  private Filter(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract AbstractCellFilter createFilter();

  public static List<String> getNames() {
    return EnumSet.allOf(Filter.class).stream()
      .map(Filter::getName)
      .collect(Collectors.toList());
  }

  public static Filter fromName(final String name) {
    return EnumSet.allOf(Filter.class).parallelStream()
      .filter(filter -> Objects.equals(name, filter.getName()))
      .findAny()
      .orElse(null);
  }
}
