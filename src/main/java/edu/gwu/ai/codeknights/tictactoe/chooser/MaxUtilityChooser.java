package edu.gwu.ai.codeknights.tictactoe.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;
import edu.gwu.ai.codeknights.tictactoe.core.Player;

public class MaxUtilityChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final Game game) {
    long maxUtility = 0L;
    Cell bestCell = null;
    final Player player = game.getNextPlayer();
    final List<Cell> cells = input.collect(Collectors.toList());
    for (final Cell cell : cells) {
      final Player initValue = cell.getPlayer();
      cell.setPlayer(player);
      final long utility = game.evaluatePlayerUtility(player);
      cell.setPlayer(initValue);
      if (utility > maxUtility) {
        maxUtility = utility;
        bestCell = cell;
      }
    }
    return bestCell;
  }
}
