package edu.gwu.ai.codeknights.tictactoe.filtering.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class BestOpenSublineFilter extends EmptyCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> inputCells = input.collect(Collectors.toList());
    final int winLength = game.getWinLength();
    final Player player = game.getNextPlayer();
    final Set<Cell> candidates = new HashSet<>();
    for (final Cell inputCell : inputCells) {
      final List<List<Cell>> lines = game.getBoard().findLinesThrough(inputCell, winLength);
      int maxLength = 0;
      List<Cell> maxSubline = null;
      for (final List<Cell> line : lines) {
        final List<Cell> subline = game.getLongestOpenSublineForPlayer(line, player);
        final int len = subline.size();
        if (subline.size() > maxLength) {
          maxLength = len;
          maxSubline = subline;
        }
      }
      if (maxSubline != null) {
        candidates.addAll(maxSubline);
      }
    }
    return super.filterCells(inputCells.stream(), game)
      .filter(cell -> candidates.contains(cell));
  }
}
