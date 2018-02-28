package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class WithNeighborSelector extends EmptyCellFilter {

  public static boolean hasPopulatedNeighbor(final int rowIdx, final int colIdx, final TicTacToeGame game) {
    return game.getBoard().getNeighborsOfCell(rowIdx, colIdx).parallelStream().anyMatch(Cell::isPopulated);
  }

  public static boolean hasPopulatedNeighbor(final Cell cell, final TicTacToeGame game) {
    return hasPopulatedNeighbor(cell.getRowIdx(), cell.getColIdx(), game);
  }

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return super.filterCells(input, game).filter(cell -> hasPopulatedNeighbor(cell, game));
  }
}
