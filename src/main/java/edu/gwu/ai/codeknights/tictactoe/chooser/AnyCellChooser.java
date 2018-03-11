package edu.gwu.ai.codeknights.tictactoe.chooser;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

import java.util.stream.Stream;

public class AnyCellChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    return input.findAny().orElse(null);
  }
}
