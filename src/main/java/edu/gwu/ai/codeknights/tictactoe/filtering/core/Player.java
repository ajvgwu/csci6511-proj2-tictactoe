package edu.gwu.ai.codeknights.tictactoe.filtering.core;

import edu.gwu.ai.codeknights.tictactoe.filtering.chooser.AbstractCellChooser;

public class Player {

  private final int id;
  private final char marker;

  private AbstractCellChooser chooser;
  private AbstractCellChooser fallbackChooser;

  public Player(final int id, final char marker) {
    this.id = id;
    this.marker = marker;

    chooser = null;
    fallbackChooser = null;
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

  public AbstractCellChooser getFallbackChooser() {
    return fallbackChooser;
  }

  public void setFallbackChooser(final AbstractCellChooser fallbackChooser) {
    this.fallbackChooser = fallbackChooser;
  }

  public Cell chooseCell(final TicTacToeGame game) {
    Cell choice = null;
    if (chooser != null) {
      choice = chooser.chooseCell(game);
    }
    if (choice == null && fallbackChooser != null) {
      choice = fallbackChooser.chooseCell(game);
    }
    if (choice == null) {
      choice = game.getBoard().getEmptyCells().parallelStream()
        .findAny()
        .orElse(null);
    }
    return choice;
  }

  @Override
  public String toString() {
    return String.valueOf(marker) + "(id=" + String.valueOf(id) + ")";
  }
}
