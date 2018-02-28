package edu.gwu.ai.codeknights.tictactoe.selector;

public class Play {

  private final TicTacToeGame game;
  private final Player player;
  private final Cell cell;

  public Play(final TicTacToeGame game, final Player player, final Cell cell) {
    this.game = game;
    this.cell = cell;
    this.player = player;
  }

  public TicTacToeGame getGame() {
    return game;
  }

  public Cell getCell() {
    return cell;
  }

  public Player getPlayer() {
    return player;
  }
}
