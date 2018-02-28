package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class RandomCellChooser extends AbstractCellChooser {

  public static final String NAME = "RandomCellChooser";

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    if (cells.size() > 0) {
      final Cell random = cells.get(new Random().nextInt(cells.size()));
      return random;
    }
    else {
      return null;
    }
  }
}
