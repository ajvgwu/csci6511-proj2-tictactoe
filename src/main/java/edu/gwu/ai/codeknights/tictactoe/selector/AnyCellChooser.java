package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class AnyCellChooser extends AbstractCellChooser {

  public static final String NAME = "AnyCellChooser";

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    return input.findAny().orElse(null);
  }
}
