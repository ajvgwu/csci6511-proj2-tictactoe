package edu.gwu.ai.codeknights.tictactoe.selector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomChooser implements PlayChooser {

  public static final String NAME = "RandomChooser";

  @Override
  public Play choosePlay(final TicTacToeGame game) {
    final List<Cell> cells = new EmptyCellSelector().selectCells(game).collect(Collectors.toList());
    final Cell random = cells.get(new Random().nextInt(cells.size()));
    return new Play(game.getNextPlayer(), random);
  }
}
