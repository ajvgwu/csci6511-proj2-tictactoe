package edu.gwu.ai.codeknights.tictactoe.selector;

public class Player {

  private final int id;
  private final char marker;

  private AbstractCellChooser chooser;

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

  public AbstractCellChooser getChooser() {
    return chooser;
  }

  public void setChooser(final AbstractCellChooser chooser) {
    this.chooser = chooser;
  }

  public Cell chooseCell(final TicTacToeGame game) {
    return chooser != null ? chooser.chooseCell(game) : null;
  }

  @Override
  public String toString() {
    return String.valueOf(marker) + "(id=" + String.valueOf(id) + ")";
  }
}
