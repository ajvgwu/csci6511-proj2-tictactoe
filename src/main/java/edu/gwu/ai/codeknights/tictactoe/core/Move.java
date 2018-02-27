package edu.gwu.ai.codeknights.tictactoe.core;

import java.util.Objects;

/**
 * @author zhiyuan
 */
public class Move {

  public final int rowIdx;
  public final int colIdx;
  public Integer player;
  private Long score;

  public Move(final int rowIdx, final int colIdx, final Integer player, final
  Long score) {
    this.rowIdx = rowIdx;
    this.colIdx = colIdx;
    this.player = player;

    this.score = score;
  }

  public Long getScore() {
    return score;
  }

  public void setScore(final Long score) {
    this.score = score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Move move = (Move) o;
    return rowIdx == move.rowIdx &&
            colIdx == move.colIdx &&
            Objects.equals(player, move.player) &&
            Objects.equals(score, move.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rowIdx, colIdx, player, score);
  }

  @Override
  public String toString() {
    return String.format("player %d at (%d,%d) scores %d", player, rowIdx, colIdx, score);
  }
}
