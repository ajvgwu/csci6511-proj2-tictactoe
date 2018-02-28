package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.List;

import edu.gwu.ai.codeknights.tictactoe.core.Game;

public interface CellSelector {

  public List<Cell> selectCells(Game game);
}
