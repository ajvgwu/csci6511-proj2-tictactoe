package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pmw.tinylog.Logger;

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
    if (board.isEmpty()) {
      final Cell centerCell = findCenterCell(cells, dim);
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
    // TODO

    // Rule 4: create a winnable fork (win in two ways)
    // TODO

    // Rule 5: prevent opponent fork
    // TODO

    // Rule 6: maximize win potential
    // TODO

    // TODO: finish
    Logger.debug("TODO: finish RuleBasedChooser.choosePlay()");
    return null;
  }

  public static Cell findCenterCell(final List<Cell> cells, final int dim) {
    final int center = (int) (dim / 2);
    Cell centerCell = cells.parallelStream()
      .filter(cell -> Math.abs(cell.getRowIdx() - center) < 1 && Math.abs(cell.getColIdx() - center) < 1)
      .findAny()
      .orElse(null);
    if (centerCell == null) {
      centerCell = cells.parallelStream()
        .filter(cell -> Math.abs(cell.getRowIdx() - center) <= 1 && Math.abs(cell.getColIdx() - center) <= 1)
        .findAny()
        .orElse(null);
    }
    if (centerCell != null) {
      return centerCell;
    }
    return null;
  }

  public static Cell findWinningCell(final TicTacToeGame game, final List<Cell> cells, final int winLength,
    final Player player) {
    return cells.parallelStream()
      .filter(cell -> {

        // Find all lines through this cell
        final List<List<Cell>> lines = game.getBoard().findLinesThrough(cell, winLength);

        // Find those lines where the player would win by playing on this cell
        return lines.parallelStream()
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
}
