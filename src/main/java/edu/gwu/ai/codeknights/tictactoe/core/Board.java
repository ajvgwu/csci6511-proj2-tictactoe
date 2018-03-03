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
   * Get all neighbors of the cell at the given coordinates. There may be up to 8 such cells, one in each possible
   * direction. Cells on the edges or corners of the board will have less than 8 neighbors.
   *
   * NOTE: there is no guarantee as to the ordering of the returned list, but it typically starts with the north-west
   *       neighbor (if it exists) and proceeds in a clockwise direction.
   *
   * @param rowIdx zero-based index of the row
   * @param colIdx zero-based index of the column
   *
   * @return an unmodifiable list of neighbors of the cell at the given coordinates
   */
  public List<Cell> getNeighborsOfCell(final int rowIdx, final int colIdx) {
    final List<Cell> cells = new ArrayList<>();
    if (rowIdx > 0 && colIdx > 0) {
      cells.add(matrix[rowIdx - 1][colIdx - 1]);
    }
    if (rowIdx > 0) {
      cells.add(matrix[rowIdx - 1][colIdx]);
    }
    if (rowIdx > 0 && colIdx + 1 < dim) {
      cells.add(matrix[rowIdx - 1][colIdx + 1]);
    }
    if (colIdx > 0) {
      cells.add(matrix[rowIdx][colIdx - 1]);
    }
    if (colIdx + 1 < dim) {
      cells.add(matrix[rowIdx][colIdx + 1]);
    }
    if (rowIdx + 1 < dim && colIdx > 0) {
      cells.add(matrix[rowIdx + 1][colIdx - 1]);
    }
    if (rowIdx + 1 < dim) {
      cells.add(matrix[rowIdx + 1][colIdx]);
    }
    if (rowIdx + 1 < dim && colIdx + 1 < dim) {
      cells.add(matrix[rowIdx + 1][colIdx + 1]);
    }
    return Collections.unmodifiableList(cells);
  }

  /**
   * Delegates to {@link #getNeighborsOfCell(int, int)}.
   *
   * @param cell the cell
   *
   * @see #getNeighborsOfCell(int, int)
   */
  public List<Cell> getNeighborsOfCell(final Cell cell) {
    return getNeighborsOfCell(cell.getRowIdx(), cell.getColIdx());
  }

  /**
   * Get all cells on the board.
   *
   * NOTE: there is no guarantee as to the ordering of the returned list, but it typically starts with the cell at
   *       {@code (0,0)} (top-left corner) and proceeds left-to-right along each row and then top-to-bottom down the
   *       board to the last cell at {@code (dim-1,dim-1)}.
   *
   * @return an unmodifiable list of all cells
   */
  public List<Cell> getAllCells() {
    return Collections.unmodifiableList(allCells);
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
   * Get the cells in the given row. These are guaranteed to be in order.
   * For example, {@code getRow(0).get(0)} will be the cell at {@code (0,0)}.
   *
   * @param idx zero-based row index
   *
   * @return an unmodifiable list of the given row of cells
   */
  public List<Cell> getRow(final int idx) {
    return Collections.unmodifiableList(rows.get(idx));
  }

  /**
   * Get all rows on the board.
   *
   * @return an unmodifiable list of all rows
   */
  public List<List<Cell>> getRows() {
    return Collections.unmodifiableList(rows);
  }

  /**
   * Get the cells in the given column. These are guaranteed to be in order.
   * For example, {@code getCol(0).get(0)} will be the cell at {@code (0,0)}.
   *
   * @param idx zero-based column index
   *
   * @return an unmodifiable list of the given column of cells
   */
  public List<Cell> getCol(final int idx) {
    return Collections.unmodifiableList(cols.get(idx));
  }

  /**
   * Get all columns on the board.
   *
   * @return an unmodifiable list of all columns
   */
  public List<List<Cell>> getCols() {
    return Collections.unmodifiableList(cols);
  }

  /**
   * Get all diagonals on the board. This includes all lines going from top-left to bottom-right direction and also all
   * lines going from bottom-left to top-right direction.
   *
   * @return an unmodifiable list of all diagonals
   */
  public List<List<Cell>> getDiagonals() {
    return Collections.unmodifiableList(diagonals);
  }

  /**
   * Get all straight lines of cells on the board. This includes all rows, all columns, and all diagonals of both
   * directions.
   *
   * @return an unmodifiable list of all lines
   */
  public List<List<Cell>> getAllLines() {
    return Collections.unmodifiableList(allLines);
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
   * Get all lines that contain the cell at the given coordinates.
   *
   * @param rowIdx zero-based row index
   * @param colIdx zero-based column index
   * @param minLength minimum line length
   *
   * @return a list of lines
   */
  public List<List<Cell>> findLinesThrough(final int rowIdx, final int colIdx, final int minLength) {
    return getLinesAtLeastLength(minLength).stream()
      .filter(line -> line.stream().anyMatch(cell -> cell.getRowIdx() == rowIdx && cell.getColIdx() == colIdx))
      .collect(Collectors.toList());
  }

  /**
   * Delegates to {@link #findLinesThrough(int, int, int)}.
   *
   * @param cell the cell
   *
   * @see #findLinesThrough(int, int, int)
   */
  public List<List<Cell>> findLinesThrough(final Cell cell, final int minLength) {
    return findLinesThrough(cell.getRowIdx(), cell.getColIdx(), minLength);
  }

  /**
   * Check if all of the cells are empty (see {@link Cell#isEmpty()}).
   *
   * @return {@code true} if all cells are empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return allCells.stream().allMatch(Cell::isEmpty);
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
   * Get a unique string representing the state of the board, suitable for hashing.
   *
   * NOTE: The implementation may change, but it typically constructs a string with one character per cell from
   *       top-left to bottom-right using row-major ordering. If a cell is populated, {@link Player#getMarker()} is
   *       used; otherwise, '.' is used.
   *
   * @return the unique string representation
   */
  public String getHash() {
    final StringBuilder bldr = new StringBuilder();
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Player mark = matrix[rowIdx][colIdx].getPlayer();
        bldr.append(mark != null ? mark.getMarker() : ".");
      }
    }
    return bldr.toString();
  }

  /**
   * Create an identical copy of the board (but with its own cell objects).
   *
   * @return the copy
   */
  public Board getCopy() {
    final Board copy = new Board(dim);
    for (final Cell cell : allCells) {
      copy.getCell(cell.getRowIdx(), cell.getColIdx()).setPlayer(cell.getPlayer());
    }
    return copy;
  }

  /**
   * Get a string representation of the board, which shows each cell and its contents in a square, multi-line format.
   *
   * @return a string representing the board
   */
  @Override
  public String toString() {
    final StringBuilder bldr = new StringBuilder();
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
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
