package edu.gwu.ai.codeknights.tictactoe.selector;

public class TicTacToeGame {

  private final int dim;

  private final Board board;

  public TicTacToeGame(final int dim) {
    this.dim = dim;

    board = new Board(dim);
  }

  public int getDim() {
    return dim;
  }

  public Board getBoard() {
    return board;
  }
}
