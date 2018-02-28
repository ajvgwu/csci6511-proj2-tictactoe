package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.List;

import edu.gwu.ai.codeknights.tictactoe.core.Game;

public interface CellFilter {

  public List<Cell> filterCells(final List<Cell> input, final Game game);
}
