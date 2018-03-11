package edu.gwu.ai.codeknights.tictactoe.filter;

import edu.gwu.ai.codeknights.tictactoe.core.Cell;
import edu.gwu.ai.codeknights.tictactoe.core.Game;

import java.util.stream.Stream;

public class PopulatedNeighborFilter extends EmptyCellFilter {

  @Override
  public Stream<Cell> filterCells(final Stream<Cell> input, final Game game) {
    return super.filterCells(input, game).filter(cell -> hasPopulatedNeighbor(cell, game));
  }

  public static boolean hasPopulatedNeighbor(final int rowIdx, final int colIdx, final Game game) {
    return game.getBoard().getNeighborsOfCell(rowIdx, colIdx).stream().anyMatch(Cell::isPopulated);
  }

  public static boolean hasPopulatedNeighbor(final Cell cell, final Game game) {
    return hasPopulatedNeighbor(cell.getRowIdx(), cell.getColIdx(), game);
  }
}
