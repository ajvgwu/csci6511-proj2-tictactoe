package edu.gwu.ai.codeknights.tictactoe.selector;

public class Player {

  private final int id;
  private final char marker;

  private PlayChooser chooser;

  public Player(final int id, final char marker) {
    this.id = id;
    this.marker = marker;

    chooser = null;
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

  public void setChooser(final PlayChooser chooser) {
    this.chooser = chooser;
  }

  @Override
  public String toString() {
    return String.valueOf(marker) + "(id=" + String.valueOf(id) + ")";
  }
}
