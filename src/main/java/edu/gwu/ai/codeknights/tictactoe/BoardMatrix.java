package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BoardMatrix {

  private final int dim;
  private final Integer[][] rowMajorMatrix;
  private final Integer[][] colMajorMatrix;

  public BoardMatrix(final int dim, final Integer[][] rowMajMat) {
    this.dim = dim;
    rowMajorMatrix = new Integer[dim][dim];
    colMajorMatrix = new Integer[dim][dim];

    // Populate both row-major and column-major matrices
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (rowMajMat.length > i && rowMajMat[i].length > j) {
          rowMajorMatrix[i][j] = rowMajMat[i][j];
        }
        if (rowMajMat.length > j && rowMajMat[j].length > i) {
          colMajorMatrix[i][j] = rowMajMat[j][i];
        }
      }
    }
  }

  public int getDim() {
    return dim;
  }

  public Integer getCellValue(final int rowIdx, final int colIdx) {
    return rowMajorMatrix[rowIdx][colIdx];
  }

  public void setCellValue(final int rowIdx, final int colIdx, final Integer value) {
    rowMajorMatrix[rowIdx][colIdx] = value;
    colMajorMatrix[colIdx][rowIdx] = value;
  }

  protected Integer[][] getAllRows() {
    return rowMajorMatrix;
  }

  public Integer[] getRow(final int idx) {
    return rowMajorMatrix[idx];
  }

  public Integer[] getCol(final int idx) {
    return colMajorMatrix[idx];
  }

  public Integer[] getDiag(final int idx) {
    final List<Integer> diag = new ArrayList<>();
    int rowIdx = Math.max(0, 0 - idx);
    int colIdx = rowIdx + idx;
    while (rowIdx < dim && colIdx < dim) {
      diag.add(getCellValue(rowIdx, colIdx));
      rowIdx++;
      colIdx++;
    }
    return diag.toArray(new Integer[diag.size()]);
  }

  public Game.Move[] getDiagMoves(final int idx){
      final List<Game.Move> diag = new ArrayList<>();
      int rowIdx = Math.max(0, 0 - idx);
      int colIdx = rowIdx + idx;
      while (rowIdx < dim && colIdx < dim) {
          diag.add(new Game.Move(rowIdx, colIdx, getCellValue(rowIdx, colIdx), null));
          rowIdx++;
          colIdx++;
      }
      return diag.toArray(new Game.Move[diag.size()]);
  }

  public Integer[] getAntiDiag(final int idx) {
    final List<Integer> diag = new ArrayList<>();
    int rowIdx = Math.min(dim - 1, dim - idx - 1);
    int colIdx = Math.max(0, 0 - idx);
    while (rowIdx >= 0 && colIdx < dim) {
      diag.add(getCellValue(rowIdx, colIdx));
      rowIdx--;
      colIdx++;
    }
    return diag.toArray(new Integer[diag.size()]);
  }

    public Game.Move[] getAntiDiagMoves(final int idx) {
        final List<Game.Move> diag = new ArrayList<>();
        int rowIdx = Math.min(dim - 1, dim - idx - 1);
        int colIdx = Math.max(0, 0 - idx);
        while (rowIdx >= 0 && colIdx < dim) {
            diag.add(new Game.Move(rowIdx, colIdx, getCellValue(rowIdx, colIdx), null));
            rowIdx--;
            colIdx++;
        }
        return diag.toArray(new Game.Move[diag.size()]);
    }

    public Map<String, Integer[]> getAllLines(final int minLength) {
        final Map<String, Integer[]> lineMap = new LinkedHashMap<>();
        if (dim >= minLength) {
            for (int i = 0; i < dim; i++) {
                lineMap.put("rowIdx=" + String.valueOf(i), getRow(i));
                lineMap.put("colIdx=" + String.valueOf(i), getCol(i));
            }
            for (int i = 0 - dim + minLength; i <= dim - minLength; i++) {
                lineMap.put("diagIdx=" + String.valueOf(i), getDiag(i));
                lineMap.put("antidiagIdx=" + String.valueOf(i), getAntiDiag(i));
            }
        }
        return lineMap;
    }

    public Map<String, Game.Move[]> getAllLinesOfMove(final int minLength) {
        final Map<String, Game.Move[]> lines = new LinkedHashMap<>();
        if (dim >= minLength) {
            for (int i = 0; i < dim; i++) {
                Game.Move[] row = new Game.Move[dim];
                Game.Move[] col = new Game.Move[dim];

                Integer[] rowValues = getRow(i);
                Integer[] colValues = getCol(i);
                for (int j = 0; j < dim; j++) {
                    row[j] = new Game.Move(i,j,rowValues[j],null);
                    col[j] = new Game.Move(j,i,colValues[j],null);
                }

                lines.put("rowIdx="+String.valueOf(i),row);
                lines.put("colIdx="+String.valueOf(i),col);
            }
            // ignore lines that are shorter than win length
            for (int i = 0 - dim + minLength; i <= dim - minLength; i++) {
                lines.put("diagIdx=" + String.valueOf(i),getDiagMoves(i));
                lines.put("antidiagIdx=" + String.valueOf(i),getAntiDiagMoves(i));
            }
        }
        return lines;
    }

    public Map<String, Game.Move[]> getAllLinesOfMove() {
        return getAllLinesOfMove(1);
    }

  public Map<String, Integer[]> getAllLines() {
    return getAllLines(1);
  }

  public long getHash(final int nextPlayer) {
    long hash = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        final Integer value = rowMajorMatrix[i][j];
        if (value != null) {
          hash += Math.pow(3, i * dim + j) * (value + 1);
        }
      }
    }
    hash += Math.pow(3, dim * dim) * (nextPlayer + 1);
    return hash;
  }

  public BoardMatrix getCopy() {
    return new BoardMatrix(dim, rowMajorMatrix);
  }

  @Override
  public String toString() {
    final StringBuilder bldr = new StringBuilder();
    for (int i = 0; i < dim; i++) {
      if (bldr.length() > 0) {
        bldr.append("\n");
      }
      for (int j = 0; j < dim; j++) {
        final Integer value = getCellValue(i, j);
        bldr.append(" ").append(String.valueOf(value)).append(" ");
      }
    }
    return bldr.toString();
  }
}
