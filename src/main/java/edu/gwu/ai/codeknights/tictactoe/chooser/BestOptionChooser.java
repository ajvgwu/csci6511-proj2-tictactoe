package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.Arrays;

import edu.gwu.ai.codeknights.tictactoe.filter.BestOpenSublineFilter;
import edu.gwu.ai.codeknights.tictactoe.filter.PopulatedNeighborFilter;

public class BestOptionChooser extends CompositeFilteringChooser {

  public BestOptionChooser() {
    super(Arrays.asList(new PopulatedNeighborFilter(), new BestOpenSublineFilter()),
      new AlphaBetaPruningChooser());
  }
}
