package edu.gwu.ai.codeknights.tictactoe.selector;

public class Play {

  private final Player player;
  private final Cell cell;

  public Play(final Player player, final Cell cell) {
    this.cell = cell;
    this.player = player;
  }

  public Cell getCell() {
    return cell;
  }

  public Player getPlayer() {
    return player;
  }
}
