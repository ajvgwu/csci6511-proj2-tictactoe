package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public interface CellFilter {

  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game);
}
