package edu.gwu.ai.codeknights.tictactoe;

import org.junit.Assert;
import org.junit.Test;

public class BoardMatrixTest extends AbstractTest {

  @Test
  public void testBoardMatrix_getCellValue_setCellValue() {
    for (final Integer[][] board : getEmptyBoards()) {
      final int dim = board.length;
      final BoardMatrix matrix = new BoardMatrix(dim, board);
      for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
        for (int colIdx = 0; colIdx < dim; colIdx++) {
          // Check initial value
          final Integer initValue = matrix.getCellValue(rowIdx, colIdx);
          Assert.assertNull(initValue);

          // Assign a new value
          Integer newValue = initValue;
          if ((rowIdx + colIdx) % 2 == 0) {
            newValue = TicTacToeGame.FIRST_PLAYER_VALUE;
          }
          else {
            newValue = TicTacToeGame.OTHER_PLAYER_VALUE;
          }
          matrix.setCellValue(rowIdx, colIdx, newValue);
          Assert.assertEquals(newValue.intValue(), matrix.getCellValue(rowIdx, colIdx).intValue());

          // Re-assign initial value
          matrix.setCellValue(rowIdx, colIdx, initValue);
          Assert.assertEquals(initValue, matrix.getCellValue(rowIdx, colIdx));
        }
      }
    }
    for (final Integer[][] board : getSequenceBoards()) {
      final int dim = board.length;
      final BoardMatrix matrix = new BoardMatrix(dim, board);
      for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
        for (int colIdx = 0; colIdx < dim; colIdx++) {
          // Check initial value
          final Integer initValue = matrix.getCellValue(rowIdx, colIdx);
          Assert.assertEquals(rowIdx * dim + colIdx, initValue.intValue());

          // Un-assign value
          matrix.setCellValue(rowIdx, colIdx, null);
          Assert.assertNull(matrix.getCellValue(rowIdx, colIdx));

          // Re-assign initial value
          matrix.setCellValue(rowIdx, colIdx, initValue);
          Assert.assertEquals(initValue, matrix.getCellValue(rowIdx, colIdx));
        }
      }
    }
  }

  @Test
  public void testBoardMatrix_getRow_getCol() {
    for (final Integer[][] board : getSequenceBoards()) {
      final int dim = board.length;
      final BoardMatrix matrix = new BoardMatrix(dim, board);
      for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
        final Integer[] row = matrix.getRow(rowIdx);
        for (int colIdx = 0; colIdx < dim; colIdx++) {
          // Check initial value
          final Integer value = row[colIdx];
          Assert.assertEquals(rowIdx * dim + colIdx, value.intValue());

          // Check relative ordering
          if (colIdx > 0) {
            Assert.assertTrue(row[colIdx] > row[colIdx - 1]);
          }
        }
      }
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Integer[] col = matrix.getCol(colIdx);
        for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
          // Check initial value
          final Integer value = col[rowIdx];
          Assert.assertEquals(rowIdx * dim + colIdx, value.intValue());

          // Check relative ordering
          if (rowIdx > 0) {
            Assert.assertTrue(col[rowIdx] > col[rowIdx - 1]);
          }
        }
      }
    }
  }

  // TODO: test extracting diagonals and anti-diagonals
  // TODO: test fetching all "lines"
}
