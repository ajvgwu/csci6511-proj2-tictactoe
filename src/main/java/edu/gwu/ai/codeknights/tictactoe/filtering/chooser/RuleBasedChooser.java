package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Board;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class RuleBasedChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> cells = input.collect(Collectors.toList());

    // Rule 1: first move goes near the center
    final Board board = game.getBoard();
    final int dim = game.getDim();
    final int center = (int) (dim / 2);
    if (board.isEmpty()) {
      final Cell centerCell = findCellNear(cells, center, center, Math.max(2, (int) (dim / 4)));
      if (centerCell != null) {
        return centerCell;
      }
    }

    // Rule 2: win if immediately possible
    final int winLength = game.getWinLength();
    final Player player = game.getNextPlayer();
    final Cell winningCell = findWinningCell(game, cells, winLength, player);
    if (winningCell != null) {
      return winningCell;
    }

    // Rule 3a: block opponent if he can win
    final Player opponent = game.getOtherPlayer(player);
    final Cell losingCell = findWinningCell(game, cells, winLength, opponent);
    if (losingCell != null) {
      return losingCell;
    }

    // Rule 3b: block opponent early if he can win in 2 moves
    final Cell loseInTwoCell = findLossInTwo(game, cells, winLength, player);
    if (loseInTwoCell != null) {
      return loseInTwoCell;
    }

    // TODO: any other rules ???

    // No choice made
    return null;
  }

  public static Cell findCellNear(final List<Cell> cells, final int rowIdx, final int colIdx, final int maxRadius) {
    for (int i = 0; i < maxRadius; i++) {
      final int curRadius = i;
      final Cell closeCell = cells.stream()
        .filter(
          cell -> Math.abs(cell.getRowIdx() - rowIdx) <= curRadius && Math.abs(cell.getColIdx() - colIdx) <= curRadius)
        .findAny()
        .orElse(null);
      if (closeCell != null) {
        return closeCell;
      }
    }
    return null;
  }

  public static Cell findWinningCell(final TicTacToeGame game, final List<Cell> cells, final int winLength,
    final Player player) {
    return cells.stream()
      .filter(cell -> {

        // Find all lines through this cell
        final List<List<Cell>> lines = game.getBoard().findLinesThrough(cell, winLength);

        // Find those lines where the player would win by playing on this cell
        return lines.stream()
          .anyMatch(line -> {
            if (cell.isEmpty()) {
              cell.setPlayer(player);
              final boolean didWin = game.didPlayerWinOnLine(player, line);
              cell.setPlayer(null);
              return didWin;
            }
            return false;
          });
      })
      .findAny()
      .orElse(null);
  }

  public static Cell findLossInTwo(final TicTacToeGame game, final List<Cell> cells, final int winLength,
    final Player player) {
    final Board board = game.getBoard();
    final int dim = game.getDim();
    final int center = (int) (dim / 2);
    final Player opponent = game.getOtherPlayer(player);
    for (int i = 0; i < cells.size(); i++) {
      final Cell firstCell = cells.get(i);
      if (firstCell.isEmpty()
        && game.getBoard().getNeighborsOfCell(firstCell).stream().anyMatch(cell -> cell.isPopulatedBy(opponent))) {
        for (int j = 1; j < cells.size(); j++) {
          if (j != i) {
            final Cell secondCell = cells.get(j);
            if (secondCell.isEmpty()
              && board.getNeighborsOfCell(secondCell).stream().anyMatch(cell -> cell.isPopulatedBy(opponent))) {
              firstCell.setPlayer(opponent);
              secondCell.setPlayer(opponent);
              final boolean didLose = game.didPlayerWin(opponent);
              firstCell.setPlayer(null);
              secondCell.setPlayer(null);
              if (didLose) {
                final int distFromCenter1 = Math.abs(firstCell.getRowIdx() - center)
                  + Math.abs(firstCell.getColIdx() - center);
                final int distFromCenter2 = Math.abs(secondCell.getRowIdx() - center)
                  + Math.abs(secondCell.getColIdx() - center);
                return distFromCenter2 < distFromCenter1 ? secondCell : firstCell;
              }
            }
          }
        }
      }
    }
    return null;
  }
}
