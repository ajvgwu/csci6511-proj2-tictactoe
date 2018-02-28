package edu.gwu.ai.codeknights.tictactoe.selector;

import java.text.MessageFormat;

public class TicTacToeGame {

  private final int dim;
  private final int winLength;
  private final Player player1;
  private final Player player2;

  private final Board board;

  public TicTacToeGame(final int dim, final int winLength, final Player player1, final Player player2) {
    this.dim = dim;
    this.winLength = winLength;
    this.player1 = player1;
    this.player2 = player2;

    board = new Board(dim);
  }

  public int getDim() {
    return dim;
  }

  public int getWinLength() {
    return winLength;
  }

  public Player getPlayer1() {
    return player1;
  }

  public Player getPlayer2() {
    return player2;
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public String toString() {
    return new StringBuilder().append(MessageFormat.format("dim={1}, winLength={2}, player1={3}, player2={4}", dim,
      winLength, player1.getMarker(), player2.getMarker())).append("\n").append(board).toString();
  }
}
