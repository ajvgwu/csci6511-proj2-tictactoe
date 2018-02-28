package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.Objects;

public class TicTacToeGame {

  private final int dim;
  private final int winLength;
  private final long gameId;
  private final Player player1;
  private final Player player2;

  private final Board board;

  public TicTacToeGame(final int dim, final int winLength, final long gameId, final Player player1,
    final Player player2) {
    this.dim = dim;
    this.winLength = winLength;
    this.gameId = gameId;
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

  public long getGameId() {
    return gameId;
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

  public void populate(final String[] args) {
    final String player1Mark = String.valueOf(player1.getMarker());
    final String player2Mark = String.valueOf(player2.getMarker());
    int argIdx = 0;
    for (int rowIdx = 0; rowIdx < dim && argIdx < args.length; rowIdx++) {
      for (int colIdx = 0; colIdx < dim && argIdx < args.length; colIdx++) {
        Player player = null;
        final String arg = args[argIdx];
        if (arg != null) {
          if (arg.equals(player1Mark)) {
            player = player1;
          }
          else if (arg.equals(player2Mark)) {
            player = player2;
          }
        }
        board.getCell(rowIdx, colIdx).setPlayer(player);
        argIdx++;
      }
    }
  }

  public boolean checkValidGameState(final boolean throwExceptionIfInvalid) throws InvalidBoardException {
    if (player1 == null || player2 == null) {
      if (throwExceptionIfInvalid) {
        throw new InvalidBoardException("player1 and player2 must be non-null");
      }
      return false;
    }
    if (player1.getId() == player2.getId() || player1.getMarker() == player2.getMarker()) {
      if (throwExceptionIfInvalid) {
        throw new InvalidBoardException("player1 and player2 must have different IDs and markers");
      }
      return false;
    }
    final int numP1 = board.countPlayer(player1);
    final int numP2 = board.countPlayer(player2);
    if (numP1 < numP2 || numP1 - numP2 > 1) {
      if (throwExceptionIfInvalid) {
        throw new InvalidBoardException("player1 may have 0 or 1 moves more than player2");
      }
      return false;
    }
    return true;
  }

  public boolean isValidGameState() {
    try {
      return checkValidGameState(false);
    }
    catch (final InvalidBoardException e) {
      // Should not happen
      return false;
    }
  }

  public boolean didPlayerWin(final Player player) {
    return board.getLinesAtLeastLength(winLength).parallelStream().anyMatch(line -> {
      int numMatch = 0;
      for (int idx = 0; idx < line.size(); idx++) {
        if (Objects.equals(player, line.get(idx).getPlayer())) {
          numMatch++;
        }
        if (numMatch >= winLength) {
          break;
        }
      }
      return numMatch >= winLength;
    });
  }

  public boolean didAnyWin() {
    return didPlayerWin(player1) || didPlayerWin(player2);
  }

  public boolean isGameOver() {
    return didAnyWin() || board.isFull();
  }

  public Player getNextPlayer() {
    int p1Count = 0;
    int p2Count = 0;
    for (final Cell cell : board.getAllCells()) {
      final Player cellPlayer = cell.getPlayer();
      if (player1.equals(cellPlayer)) {
        p1Count++;
      }
      else if (player2.equals(cellPlayer)) {
        p2Count++;
      }
    }
    return p1Count <= p2Count ? player1 : player2;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("dim=").append(dim).append(", winLength=").append(winLength).append(", gameId=")
      .append(gameId).append(", player1=").append(player1).append(", player2=").append(player2)
      .append(", isValidGameState=").append(isValidGameState()).append("\n").append(board).toString();
  }
}
