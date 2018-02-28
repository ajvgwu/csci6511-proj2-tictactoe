package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.stream.Stream;

public class WithNeighborSelector implements CellSelector, CellFilter {

  public static boolean hasPopulatedNeighbor(final int rowIdx, final int colIdx, final TicTacToeGame game) {
    final int dim = game.getDim();
    return rowIdx > 0 && colIdx > 0 && !game.getBoard().getCell(rowIdx - 1, colIdx - 1).isEmpty()
      || rowIdx > 0 && !game.getBoard().getCell(rowIdx - 1, colIdx).isEmpty()
      || rowIdx > 0 && colIdx < dim - 1 && !game.getBoard().getCell(rowIdx - 1, colIdx + 1).isEmpty()
      || colIdx > 0 && !game.getBoard().getCell(rowIdx, colIdx - 1).isEmpty()
      || colIdx < dim - 1 && !game.getBoard().getCell(rowIdx, colIdx + 1).isEmpty()
      || rowIdx < dim - 1 && colIdx > 0 && !game.getBoard().getCell(rowIdx + 1, colIdx - 1).isEmpty()
      || rowIdx < dim - 1 && !game.getBoard().getCell(rowIdx + 1, colIdx).isEmpty()
      || rowIdx < dim - 1 && colIdx < dim - 1 && !game.getBoard().getCell(rowIdx + 1, colIdx + 1).isEmpty();
  }

  public static boolean hasPopulatedNeighbor(final Cell cell, final TicTacToeGame game) {
    return hasPopulatedNeighbor(cell.getRowIdx(), cell.getColIdx(), game);
  }

  @Override
  public Stream<Cell> selectCells(final TicTacToeGame game) {
    return game.getBoard().getAllCells().parallelStream().filter(cell -> hasPopulatedNeighbor(cell, game));
  }

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final TicTacToeGame game) {
    return input.filter(cell -> hasPopulatedNeighbor(cell, game));
  }
}
