package edu.gwu.ai.codeknights.tictactoe;

import org.junit.Assert;
import org.junit.Test;

public class TicTacToeGameTest extends AbstractTest {

  // TODO: test edge cases in constructor (passing invalid values, invalid sizes, etc.)

  @Test
  public void testTicTacToeGame_counting() throws DimensionException, StateException {
    for (final Integer[][] board : getEmptyBoards()) {
      final int dim = board.length;
      if (dim <= Game.MAX_DIM) {
        final Game game = new Game(dim, Math.min(Game.MAX_WIN_LENGTH, dim), board);
        Assert.assertEquals(dim * dim, game.countEmpty());
        Assert.assertEquals(0, game.countFirstPlayer());
        Assert.assertEquals(0, game.countOtherPlayer());

        // Assign players in alternating manner
        for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
          for (int colIdx = 0; colIdx < dim; colIdx++) {
            if ((rowIdx * dim + colIdx) % 2 == 0) {
              game.setCellValue(rowIdx, colIdx, Game.FIRST_PLAYER_VALUE);
              Assert.assertTrue("first player #moves > other player #moves",
                game.countFirstPlayer() > game.countOtherPlayer());
            }
            else {
              game.setCellValue(rowIdx, colIdx, Game.OTHER_PLAYER_VALUE);
              Assert.assertTrue("first player #moves == other player #moves",
                game.countFirstPlayer() == game.countOtherPlayer());
            }
          }
        }
      }
    }
  }

  // TODO: test checking for win conditions
  // TODO: test checking for game over conditions
}
