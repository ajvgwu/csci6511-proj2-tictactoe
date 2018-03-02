package edu.gwu.ai.codeknights.tictactoe.filtering.chooser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Board;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.Player;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class PairingChooser extends AbstractCellChooser {

  @Override
  public Cell chooseCell(final Stream<Cell> input, final TicTacToeGame game) {
    final List<Cell> cells = input.collect(Collectors.toList());
    return tryFindPair(game, cells);
  }

  public static Cell getPairedCell(final Cell cell, final TicTacToeGame game) {
    final int rowIdx = cell.getRowIdx();
    final int colIdx = cell.getColIdx();
    final int offsetRow = rowIdx % 8;
    final int offsetCol = colIdx % 8;
    Integer pairAddRow = null;
    Integer pairAddCol = null;

    // 1. Straight right
    if (offsetRow == 0 && offsetCol == 1 ||
      offsetRow == 1 && offsetCol == 5 ||
      offsetRow == 2 && offsetCol == 5 ||
      offsetRow == 3 && offsetCol == 3 ||
      offsetRow == 4 && offsetCol == 3 ||
      offsetRow == 5 && offsetCol == 7 ||
      offsetRow == 6 && offsetCol == 7 ||
      offsetRow == 7 && offsetCol == 1) {
      pairAddRow = 0;
      pairAddCol = 1;
    }

    // 2. Straight left
    else if (offsetRow == 0 && offsetCol == 2 ||
      offsetRow == 1 && offsetCol == 6 ||
      offsetRow == 2 && offsetCol == 6 ||
      offsetRow == 3 && offsetCol == 4 ||
      offsetRow == 4 && offsetCol == 4 ||
      offsetRow == 5 && offsetCol == 0 ||
      offsetRow == 6 && offsetCol == 0 ||
      offsetRow == 7 && offsetCol == 2) {
      pairAddRow = 0;
      pairAddCol = -1;
    }

    // 3. Straight down
    else if (offsetRow == 1 && offsetCol == 0 ||
      offsetRow == 3 && offsetCol == 1 ||
      offsetRow == 3 && offsetCol == 2 ||
      offsetRow == 7 && offsetCol == 3 ||
      offsetRow == 7 && offsetCol == 4 ||
      offsetRow == 5 && offsetCol == 5 ||
      offsetRow == 5 && offsetCol == 6 ||
      offsetRow == 1 && offsetCol == 7) {
      pairAddRow = 1;
      pairAddCol = 0;
    }

    // 4. Straight up
    else if (offsetRow == 2 && offsetCol == 0 ||
      offsetRow == 4 && offsetCol == 1 ||
      offsetRow == 4 && offsetCol == 2 ||
      offsetRow == 0 && offsetCol == 3 ||
      offsetRow == 0 && offsetCol == 4 ||
      offsetRow == 6 && offsetCol == 5 ||
      offsetRow == 6 && offsetCol == 6 ||
      offsetRow == 2 && offsetCol == 7) {
      pairAddRow = -1;
      pairAddCol = 0;
    }

    // 5. Diagonal down-right
    else if (offsetRow == 0 && offsetCol == 0 ||
      offsetRow == 1 && offsetCol == 2 ||
      offsetRow == 2 && offsetCol == 4 ||
      offsetRow == 3 && offsetCol == 6 ||
      offsetRow == 4 && offsetCol == 0 ||
      offsetRow == 5 && offsetCol == 2 ||
      offsetRow == 6 && offsetCol == 4 ||
      offsetRow == 7 && offsetCol == 6) {
      pairAddRow = 1;
      pairAddCol = 1;
    }

    // 6. Diagonal up-left
    else if (offsetRow == 0 && offsetCol == 7 ||
      offsetRow == 1 && offsetCol == 1 ||
      offsetRow == 2 && offsetCol == 3 ||
      offsetRow == 3 && offsetCol == 5 ||
      offsetRow == 4 && offsetCol == 7 ||
      offsetRow == 5 && offsetCol == 1 ||
      offsetRow == 6 && offsetCol == 3 ||
      offsetRow == 7 && offsetCol == 5) {
      pairAddRow = -1;
      pairAddCol = -1;
    }

    // 7. Diagonal down-left
    else if (offsetRow == 0 && offsetCol == 5 ||
      offsetRow == 1 && offsetCol == 3 ||
      offsetRow == 2 && offsetCol == 1 ||
      offsetRow == 3 && offsetCol == 7 ||
      offsetRow == 4 && offsetCol == 5 ||
      offsetRow == 5 && offsetCol == 3 ||
      offsetRow == 6 && offsetCol == 1 ||
      offsetRow == 7 && offsetCol == 7) {
      pairAddRow = 1;
      pairAddCol = -1;
    }

    // 8. Diagonal up-right
    else if (offsetRow == 0 && offsetCol == 6 ||
      offsetRow == 1 && offsetCol == 4 ||
      offsetRow == 2 && offsetCol == 2 ||
      offsetRow == 3 && offsetCol == 0 ||
      offsetRow == 4 && offsetCol == 6 ||
      offsetRow == 5 && offsetCol == 4 ||
      offsetRow == 6 && offsetCol == 2 ||
      offsetRow == 7 && offsetCol == 0) {
      pairAddRow = -1;
      pairAddCol = 1;
    }

    // Get paired cell if it was found and is in-bounds
    if (pairAddRow != null && pairAddCol != null) {
      final int pairRowIdx = rowIdx + pairAddRow;
      final int pairColIdx = colIdx + pairAddCol;
      final int dim = game.getDim();
      if (pairRowIdx >= 0 && pairRowIdx < dim && pairColIdx >= 0 && pairColIdx < dim) {
        return game.getBoard().getCell(pairRowIdx, pairColIdx);
      }
    }
    return null;
  }

  public static Cell tryFindPair(final TicTacToeGame game, final List<Cell> cells) {
    if (game.getDim() < 8) {
      return null;
    }
    final Board board = game.getBoard();
    final Player player = game.getNextPlayer();
    final Player opponent = game.getOtherPlayer(player);
    if (board.countPlayer(player) >= board.countPlayer(opponent)) {
      return null;
    }
    for (final Cell cell : cells) {
      final Cell pair = getPairedCell(cell, game);
      if (pair != null && pair.isPopulatedBy(opponent)) {
        return cell;
      }
    }
    return null;
  }
}
