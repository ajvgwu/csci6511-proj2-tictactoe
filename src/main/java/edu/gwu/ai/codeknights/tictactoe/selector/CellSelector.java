package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public interface CellSelector {

  public Stream<Cell> selectCells(TicTacToeGame game);
}
