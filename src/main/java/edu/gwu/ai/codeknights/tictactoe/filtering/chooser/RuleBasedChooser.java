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
    final int dim = game.getDim();
    // final int winLength = game.getWinLength(); // TODO: do we need this for any rules ???
    final Player player = game.getNextPlayer();
    final Board board = game.getBoard();

    // Rule 1: first move goes near the center
    if (board.isEmpty()) {
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
    }

    // Rule 2: win if immediately possible
    for (final Cell cell : cells) {

      // Find all lines through this cell
      final List<List<Cell>> lines = game.getBoard().findLinesThrough(cell, game.getWinLength());

      // Find those lines where the player would win by playing on this cell
      final boolean isWinningMove = lines.parallelStream()
        .anyMatch(line -> {
          if (cell.isEmpty()) {
            cell.setPlayer(player);
            final boolean didWin = game.didPlayerWinOnLine(player, line);
            cell.setPlayer(null);
            return didWin;
          }
          return false;
        });
      if (isWinningMove) {
        return cell;
      }
    }

    // Rule 3: block opponent wins at both ends of lines
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
}
