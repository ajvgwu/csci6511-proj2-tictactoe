package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A square board for a {@link Game} of Tic Tac Toe.
 *
 * @author ajv
 */
public class Board {

  private final int dim;

  private final Cell[][] matrix;
  private final List<Cell> allCells;
  private final List<List<Cell>> rows;
  private final List<List<Cell>> cols;
  private final List<List<Cell>> diagonals;
  private final List<List<Cell>> allLines;
  private final Map<Integer, List<List<Cell>>> linesAtLeastLength;

  /**
   * Construct a new square board of the given dimension.
   *
   * @param dim the dimension (number of rows and columns)
   */
  public Board(final int dim) {
    this.dim = dim;

    matrix = new Cell[dim][dim];
    allCells = new ArrayList<>();
    rows = new ArrayList<>(dim);
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Cell cell = new Cell(rowIdx, colIdx);
        matrix[rowIdx][colIdx] = cell;
        allCells.add(cell);
      }
      rows.add(Arrays.asList(matrix[rowIdx]));
    }
    cols = new ArrayList<>(dim);
    for (int colIdx = 0; colIdx < dim; colIdx++) {
      final Cell[] col = new Cell[dim];
      for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
        col[rowIdx] = matrix[rowIdx][colIdx];
      }
      cols.add(Arrays.asList(col));
    }
    diagonals = new ArrayList<>(2 * (2 * dim - 1));
    for (int i = 0 - dim + 1; i <= dim - 1; i++) {
      diagonals.add(Arrays.asList(constructDiag(matrix, i)));
      diagonals.add(Arrays.asList(constructAntiDiag(matrix, i)));
    }
    allLines = new ArrayList<>();
    allLines.addAll(rows);
    allLines.addAll(cols);
    allLines.addAll(diagonals);
    linesAtLeastLength = new HashMap<>();
    for (int i = 1; i <= dim; i++) {
      final int length = i;
      final List<List<Cell>> lines = allLines.stream()
        .filter(line -> line.size() >= length)
        .collect(Collectors.toList());
      linesAtLeastLength.put(length, lines);
    }
  }

  /**
   * Get the dimension (number of rows and columns).
   *
   * @return the dimension
   */
  public int getDim() {
    return dim;
  }

  /**
   * Retrieve the cell at the given coordinates.
   *
   * @param rowIdx zero-based index of the row
   * @param colIdx zero-based index of the column
   *
   * @return the cell at the given coordinates.
   */
  public Cell getCell(final int rowIdx, final int colIdx) {
    return matrix[rowIdx][colIdx];
  }

  /**
   * Get all empty cells.
   *
   * @return an unmodifiable list of all empty cells
   */
  public List<Cell> getEmptyCells() {
    final List<Cell> emptyCells = allCells.stream().filter(Cell::isEmpty).collect(Collectors.toList());
    return Collections.unmodifiableList(emptyCells);
  }

  /**
   * Get all lines of cells that are at least as long as the given length.
   *
   * @param length the minimum line length
   *
   * @return an unmodifiable list of lines
   */
  public List<List<Cell>> getLinesAtLeastLength(final int length) {
    final List<List<Cell>> lines = linesAtLeastLength.get(length);
    return lines != null ? Collections.unmodifiableList(lines) : Collections.emptyList();
  }

  /**
   * Check if none of the cells are empty (see {@link Cell#isEmpty()}).
   *
   * @return {@code true} if none of the cells are empty, {@code false} otherwise
   */
  public boolean isFull() {
    return allCells.stream().noneMatch(Cell::isEmpty);
  }

  /**
   * Count and return the number of empty cells on the board.
   *
   * @return the number of empty cells
   */
  public int countEmpty() {
    return allCells.stream()
      .mapToInt(cell -> cell.isEmpty() ? 1 : 0)
      .sum();
  }

  /**
   * Count and return the number of cells that are populated by the given player.
   *
   * @param player the player whose cells will be counted
   *
   * @return the number of cells populated by the player
   */
  public int countPlayer(final Player player) {
    return allCells.stream()
      .mapToInt(cell -> cell.isPopulatedBy(player) ? 1 : 0)
      .sum();
  }


  /**
   * Get a string representation of the board, which shows each cell and its contents in a square, multi-line format.
   *
   * @return a string representing the board
   */
  @Override
  public String toString() {
    final int dimStrLen = String.valueOf(dim - 1).length();
    final String idxFormat = "%" + String.valueOf(dimStrLen) + "d";
    final StringBuilder bldr = new StringBuilder();
    // TODO: add column headers ???
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      bldr.append("[").append(String.format(idxFormat, rowIdx)).append("]");
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        bldr.append(" ").append(getCell(rowIdx, colIdx)).append(" ");
      }
    }
    return bldr.toString();
  }

  private static Cell[] constructDiag(final Cell[][] matrix, final int idx) {
    final int dim = matrix.length;
    final Cell[] diag = new Cell[dim - Math.abs(idx)];
    int rowIdx = Math.max(0, 0 - idx);
    int colIdx = rowIdx + idx;
    int diagIdx = 0;
    while (rowIdx < dim && colIdx < dim) {
      diag[diagIdx] = matrix[rowIdx][colIdx];
      rowIdx++;
      colIdx++;
      diagIdx++;
    }
    return diag;
  }

  private static Cell[] constructAntiDiag(final Cell[][] matrix, final int idx) {
    final int dim = matrix.length;
    final Cell[] diag = new Cell[dim - Math.abs(idx)];
    int rowIdx = Math.min(dim - 1, dim - idx - 1);
    int colIdx = Math.max(0, 0 - idx);
    int diagIdx = 0;
    while (rowIdx >= 0 && colIdx < dim) {
      diag[diagIdx] = matrix[rowIdx][colIdx];
      rowIdx--;
      colIdx++;
      diagIdx++;
    }
    return diag;
  }
}
