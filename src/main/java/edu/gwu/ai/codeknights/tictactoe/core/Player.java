package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.Objects;

import edu.gwu.ai.codeknights.tictactoe.chooser.AbstractCellChooser;

/**
 * A player that can populate the {@link Cell}s of a Tic Tac Toe {@link Game}.
 *
 * @author ajv
 */
public class Player {

  private final int id;
  private final char marker;

  private AbstractCellChooser chooser;
  private AbstractCellChooser fallbackChooser;

  /**
   * Construct a new player with the given ID and marker.
   *
   * @param id     the player's ID
   * @param marker the player's marker
   */
  public Player(final int id, final char marker) {
    this.id = id;
    this.marker = marker;

    chooser = null;
    fallbackChooser = null;
  }

  /**
   * Get the player's ID.
   *
   * @return the player's ID
   */
  public int getId() {
    return id;
  }

  /**
   * Get the player's marker, used for the purposes of indicating that a {@link Cell} is populated by this player.
   *
   * @return the player's marker
   */
  public char getMarker() {
    return marker;
  }

  /**
   * Get the cell chooser for this player. This is the primary chooser used in {@link #chooseCell(Game)}.
   *
   * @return the player's cell chooser
   */
  public AbstractCellChooser getChooser() {
    return chooser;
  }

  /**
   * Assign a cell chooser for this player. This is the primary chooser used in {@link #chooseCell(Game)}.
   *
   * @param chooser the player's new cell chooser
   */
  public void setChooser(final AbstractCellChooser chooser) {
    this.chooser = chooser;
  }

  /**
   * Get the fallback cell chooser for this player. This is the fallback chooser used in {@link #chooseCell(Game)}.
   *
   * @return the fallback cell chooser
   */
  public AbstractCellChooser getFallbackChooser() {
    return fallbackChooser;
  }

  /**
   * Assign a fallback cell chooser for this player. This is the fallback chooser used in {@link #chooseCell(Game)}.
   *
   * @param fallbackChooser the fallback cell chooser
   */
  public void setFallbackChooser(final AbstractCellChooser fallbackChooser) {
    this.fallbackChooser = fallbackChooser;
  }

  /**
   * Use the primary chooser (see {@link #getChooser()}) to choose a cell from the given game to populate. If it does
   * not choose a valid cell, use the fallback chooser (see {@link #getFallbackChooser()}) to choose a cell. If it also
   * does not choose a valid cell, try to find any empty cell in which to play.
   *
   * @param game the game from which to select a cell
   *
   * @return the chosen cell, or {@code null} if no empty cell was found
   */
  public Cell chooseCell(final Game game) {
    Cell choice = null;
    if (chooser != null) {
      choice = chooser.chooseCell(game);
      if (choice != null && choice.isPopulated()) {
        choice = null;
      }
    }
    if (choice == null && fallbackChooser != null) {
      choice = fallbackChooser.chooseCell(game);
      if (choice != null && choice.isPopulated()) {
        choice = null;
      }
    }
    if (choice == null) {
      choice = game.getBoard().getEmptyCells().stream()
        .findAny()
        .orElse(null);
    }
    return choice;
  }

  /**
   * Get a string with uniquely identifying features of this player ({@link #marker} and {@link #id}).
   *
   * @return a string representing this player
   */
  @Override
  public String toString() {
    return String.valueOf(marker) + "(id=" + String.valueOf(id) + ")";
  }

  /**
   * Check whether the given object is a {@link Player} with the same {@link #id} and {@link #marker}.
   *
   * @param o the other object
   *
   * @return {@code true} if the given object is a {@link Player} with the same {@link #id} and {@link #marker},
   *         {@code false} otherwise
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !getClass().equals(o.getClass())) {
      return false;
    }
    final Player other = (Player) o;
    return id == other.id && marker == other.marker;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, marker);
  }
}
