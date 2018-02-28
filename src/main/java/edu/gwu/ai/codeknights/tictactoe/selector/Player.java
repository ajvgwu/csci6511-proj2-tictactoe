package edu.gwu.ai.codeknights.tictactoe.selector;

public class Player {

  private final int id;
  private final char marker;
  private final PlayChooser chooser;

  public Player(final int id, final char marker, final PlayChooser chooser) {
    this.id = id;
    this.marker = marker;
    this.chooser = chooser;
  }

  public int getId() {
    return id;
  }

  public char getMarker() {
    return marker;
  }

  public PlayChooser getChooser() {
    return chooser;
  }
}
