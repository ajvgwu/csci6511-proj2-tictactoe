package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractTest {

  public static final int MIN_BOARD_TEST_DIM = 0;
  public static final int MAX_BOARD_TEST_DIM = 21;

  private List<Integer[][]> emptyBoards;
  private List<Integer[][]> sequenceBoards;

  public AbstractTest() {
    emptyBoards = null;
    sequenceBoards = null;
  }

  public List<Integer[][]> getEmptyBoards() {
    return emptyBoards;
  }

  public List<Integer[][]> getSequenceBoards() {
    return sequenceBoards;
  }

  @Before
  public void createBoards() {
    emptyBoards = new ArrayList<>();
    sequenceBoards = new ArrayList<>();
    for (int dim = MIN_BOARD_TEST_DIM; dim <= MAX_BOARD_TEST_DIM; dim++) {
      // Create empty board
      final Integer[][] curEmptyBoard = new Integer[dim][dim];
      emptyBoards.add(curEmptyBoard);

      // Create board with ascending sequence of Integers
      final Integer[][] curSequenceBoard = new Integer[dim][dim];
      sequenceBoards.add(curSequenceBoard);
      for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
        for (int colIdx = 0; colIdx < dim; colIdx++) {
          curSequenceBoard[rowIdx][colIdx] = rowIdx * dim + colIdx;
        }
      }
    }
  }

  @After
  public void clearBoards() {
    if (emptyBoards != null) {
      emptyBoards.clear();
    }
    emptyBoards = null;

    if (sequenceBoards != null) {
      sequenceBoards.clear();
    }
    sequenceBoards = null;
  }
}
