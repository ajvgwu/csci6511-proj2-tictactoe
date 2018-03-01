package edu.gwu.ai.codeknights.tictactoe.filtering.filter;

import java.util.stream.Stream;

import edu.gwu.ai.codeknights.tictactoe.filtering.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.filtering.core.TicTacToeGame;

public class WithNeighborFilter extends EmptyCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return super.filterCells(input, game).filter(cell -> hasPopulatedNeighbor(cell, game));
  }

  public static boolean hasPopulatedNeighbor(final int rowIdx, final int colIdx, final TicTacToeGame game) {
    return game.getBoard().getNeighborsOfCell(rowIdx, colIdx).parallelStream().anyMatch(Cell::isPopulated);
  }

  public static boolean hasPopulatedNeighbor(final Cell cell, final TicTacToeGame game) {
    return hasPopulatedNeighbor(cell.getRowIdx(), cell.getColIdx(), game);
  }
}
