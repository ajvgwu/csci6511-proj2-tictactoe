package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Board {

  private final int dim;

  private final Cell[][] matrix;
  private final List<List<Cell>> rows;
  private final List<List<Cell>> cols;
  private final List<List<Cell>> diagonals;
  private final List<List<Cell>> allLines;
  private final Map<Integer, List<List<Cell>>> linesAtLeastLength;

  public Board(final int dim) {
    this.dim = dim;

    matrix = new Cell[dim][dim];
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        matrix[rowIdx][colIdx] = new Cell(rowIdx, colIdx);
      }
    }
    rows = new ArrayList<>(dim);
    for (final Cell[] row : matrix) {
      rows.add(Arrays.asList(row));
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
      final List<List<Cell>> lines = allLines.stream().filter(line -> line.size() >= length)
        .collect(Collectors.toList());
      linesAtLeastLength.put(length, lines);
    }
  }

  public int getDim() {
    return dim;
  }

  public Cell getCell(final int rowIdx, final int colIdx) {
    return matrix[rowIdx][colIdx];
  }

  public List<Cell> getRow(final int idx) {
    return Collections.unmodifiableList(rows.get(idx));
  }

  public List<List<Cell>> getRows() {
    return Collections.unmodifiableList(rows);
  }

  public List<Cell> getCol(final int idx) {
    return Collections.unmodifiableList(cols.get(idx));
  }

  public List<List<Cell>> getCols() {
    return Collections.unmodifiableList(cols);
  }

  public List<List<Cell>> getDiagonals() {
    return Collections.unmodifiableList(diagonals);
  }

  public List<List<Cell>> getAllLines() {
    return Collections.unmodifiableList(allLines);
  }

  public List<List<Cell>> getLinesAtLeastLength(final int length) {
    final List<List<Cell>> lines = linesAtLeastLength.get(length);
    return lines != null ? Collections.unmodifiableList(lines) : Collections.emptyList();
  }

  public String toStringAllLines() {
    final StringBuilder bldr = new StringBuilder();
    for (final List<Cell> line : getAllLines()) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      bldr.append(line);
    }
    return bldr.toString();
  }

  public String toStringLinesAtLeastLength(final int length) {
    final StringBuilder bldr = new StringBuilder();
    for (final List<Cell> line : getLinesAtLeastLength(length)) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      bldr.append(line);
    }
    return bldr.toString();
  }

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
