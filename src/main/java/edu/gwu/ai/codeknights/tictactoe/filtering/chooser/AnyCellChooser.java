package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class AnyCellChooser extends AbstractCellChooser {

  public static final String NAME = "AnyCellChooser";

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    return input.findAny().orElse(null);
  }
}
