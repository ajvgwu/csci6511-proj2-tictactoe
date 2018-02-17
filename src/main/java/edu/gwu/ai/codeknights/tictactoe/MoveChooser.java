package edu.gwu.ai.codeknights.tictactoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class MoveChooser {

  private boolean randomChoice;
  private final Map<Long, Integer> hashScoreMap;

  public MoveChooser() {
    randomChoice = false;
    hashScoreMap = new HashMap<>();
  }

  public boolean isRandomChoice() {
    return randomChoice;
  }

  public void setRandomChoice(final boolean randomChoice) {
    this.randomChoice = randomChoice;
  }

  protected Map<Long, Integer> getHashScoreMap() {
    return hashScoreMap;
  }

  protected List<Game.Move> findPossibleMoves(final Game game) {
    final List<Game.Move> moves = new ArrayList<>();
    final int dim = game.getDim();
    final int curPlayer = game.getNextPlayer();
    for (int rowIdx = 0; rowIdx < dim; rowIdx++) {
      for (int colIdx = 0; colIdx < dim; colIdx++) {
        final Integer value = game.getCellValue(rowIdx, colIdx);
        if (value == null) {
          moves.add(new Game.Move(rowIdx, colIdx, curPlayer, null));
        }
      }
    }
    return moves;
  }

  protected Game.Move selectMove(final List<Game.Move> moves) {
    if (moves.size() > 1 && isRandomChoice()) {
      // If many moves scored equally, choose randomly from among them
      return moves.get(new Random().nextInt(moves.size()));
    }
    else if (moves.size() > 0) {
      // Return best move
      return moves.get(0);
    }
    else {
      // No move found !!!
      return null;
    }
  }

  public abstract Game.Move findBestMove(final Game game);
}
