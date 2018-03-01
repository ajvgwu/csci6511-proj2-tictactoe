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
  INSIDE_FEASIBLE_LINE("InsideFeasibleLine") {

    @Override
    public AbstractCellFilter createFilter() {
      return new InsideFeasibleLineFilter();
    }
  },
  WITH_NEIGHBOR("WithNeighbor") {

    @Override
    public AbstractCellFilter createFilter() {
      return new WithNeighborFilter();
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
